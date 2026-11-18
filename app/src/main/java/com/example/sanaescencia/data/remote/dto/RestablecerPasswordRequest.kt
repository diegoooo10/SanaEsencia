package com.example.sanaescencia.data.remote.dto

data class RestablecerPasswordRequest(
    val token: String,
    val nuevaPassword: String
)