package com.example.m3h1.api

import com.example.m3h1.data.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("health")
    suspend fun checkHealth(): Response<HealthResponse>

    @Multipart
    @POST("upload")
    suspend fun uploadPdf(
        @Part file: MultipartBody.Part
    ): Response<ProcessResponse>

    @POST("process")
    suspend fun processDocument(): Response<ProcessResponse>

    @POST("ask")
    suspend fun askQuestion(
        @Body request: AskRequest
    ): Response<AskResponse>
}
