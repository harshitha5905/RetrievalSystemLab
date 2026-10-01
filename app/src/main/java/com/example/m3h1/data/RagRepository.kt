package com.example.m3h1.data

import com.example.m3h1.api.RetrofitInstance
import okhttp3.MultipartBody
import retrofit2.Response

class RagRepository {
    private val api = RetrofitInstance.api

    suspend fun checkHealth() = api.checkHealth()

    suspend fun uploadPdf(file: MultipartBody.Part) = api.uploadPdf(file)

    suspend fun processDocument() = api.processDocument()

    suspend fun askQuestion(question: String) = api.askQuestion(AskRequest(question))
}
