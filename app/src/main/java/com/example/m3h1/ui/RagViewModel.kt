package com.example.m3h1.ui

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.m3h1.data.AskResponse
import com.example.m3h1.data.RagRepository
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

class RagViewModel : ViewModel() {
    private val repository = RagRepository()

    var selectedFileName by mutableStateOf<String?>(null)
    var selectedFileUri by mutableStateOf<Uri?>(null)
    var processingStatus by mutableStateOf("Ready")
    var isProcessing by mutableStateOf(false)
    
    var question by mutableStateOf("")
    var answerResponse by mutableStateOf<AskResponse?>(null)
    var isAsking by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun onFileSelected(uri: Uri, context: Context) {
        selectedFileUri = uri
        selectedFileName = getFileName(uri, context)
        errorMessage = null
    }

    private fun getFileName(uri: Uri, context: Context): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (index != -1) result = cursor.getString(index)
                }
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != -1 && cut != null) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }

    fun processDocument(context: Context) {
        val uri = selectedFileUri ?: return
        
        viewModelScope.launch {
            try {
                isProcessing = true
                processingStatus = "Uploading..."
                errorMessage = null

                val file = uriToFile(uri, context)
                val requestFile = file.asRequestBody("application/pdf".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

                val uploadResponse = repository.uploadPdf(body)
                if (uploadResponse.isSuccessful) {
                    processingStatus = "Processing..."
                    val processResult = repository.processDocument()
                    if (processResult.isSuccessful) {
                        processingStatus = "Document Ready"
                    } else {
                        errorMessage = "Processing failed: ${processResult.message()}"
                        processingStatus = "Error"
                    }
                } else {
                    errorMessage = "Upload failed: ${uploadResponse.message()}"
                    processingStatus = "Error"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.localizedMessage}"
                processingStatus = "Error"
            } finally {
                isProcessing = false
            }
        }
    }

    fun askQuestion() {
        if (question.isBlank()) return
        
        viewModelScope.launch {
            try {
                isAsking = true
                errorMessage = null
                val response = repository.askQuestion(question)
                if (response.isSuccessful) {
                    answerResponse = response.body()
                } else {
                    errorMessage = "Failed to get answer: ${response.message()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.localizedMessage}"
            } finally {
                isAsking = false
            }
        }
    }

    private fun uriToFile(uri: Uri, context: Context): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, getFileName(uri, context) ?: "temp.pdf")
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        return file
    }
}
