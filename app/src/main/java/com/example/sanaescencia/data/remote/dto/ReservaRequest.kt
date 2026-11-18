package com.example.sanaescencia.data.remote.dto

data class ReservaRequest(
    val profesionalId: Long,
    val servicioId: Long,
    val fecha: String,
    val hora: String,
    val modalidad: String
)