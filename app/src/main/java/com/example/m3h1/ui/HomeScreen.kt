package com.example.m3h1.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(
    viewModel: RagViewModel,
    onNavigateToAsk: () -> Unit
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.onFileSelected(it, context) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "RAG PDF Document Assistant",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Button(
            onClick = { launcher.launch("application/pdf") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Select PDF")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Selected File: ${viewModel.selectedFileName ?: "None"}",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { viewModel.processDocument(context) },
            enabled = viewModel.selectedFileUri != null && !viewModel.isProcessing,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (viewModel.isProcessing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Process Document")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Status: ${viewModel.processingStatus}",
            color = if (viewModel.processingStatus == "Error") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
        )

        viewModel.errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onNavigateToAsk,
            enabled = viewModel.processingStatus == "Document Ready",
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Go to Ask Question")
        }
    }
}
