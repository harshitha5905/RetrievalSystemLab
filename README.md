# RAG PDF Document Assistant

A full-stack mobile application that allows users to chat with their PDF documents using Retrieval-Augmented Generation (RAG).

## Project Structure
- **app/**: Android application built with Kotlin, Jetpack Compose, and Retrofit.
- **backend/**: Python FastAPI server handling PDF processing, FAISS vector storage, and OpenAI LLM integration.

## Features
- PDF text extraction and chunking.
- Semantic search using FAISS and OpenAI Embeddings.
- Context-aware Q&A using OpenAI GPT-3.5.
- Modern Android UI with Jetpack Compose and MVVM architecture.

## Setup Instructions

### 1. Backend Setup
1. Navigate to the `backend` directory.
2. Create a `.env` file and add your OpenAI API Key:
   ```
   OPENAI_API_KEY=your_api_key_here
   ```
3. Install dependencies:
   ```bash
   pip install -r requirements.txt
   ```
4. Start the server:
   ```bash
   python main.py
   ```
   *The server runs on http://0.0.0.0:8000*

### 2. Android App Setup
1. Open the project in Android Studio.
2. If using a real device, update `RetrofitInstance.kt` with your computer's local IP address.
3. Build and run the app.

## How to Use
1. Open the app and click **Select PDF**.
2. Click **Process Document** to upload and index the file.
3. Once ready, navigate to the **Ask Question** screen.
4. Type your question and get answers grounded in your document's content.
