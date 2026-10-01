package com.example.m3h1.data

import com.google.gson.annotations.SerializedName

data class AskRequest(
    val question: String
)

data class AskResponse(
    val answer: String,
    val sources: List<String>
)

data class HealthResponse(
    val status: String
)

data class ProcessResponse(
    val message: String
)
