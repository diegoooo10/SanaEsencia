package com.example.sanaescencia.data.model

data class Reserva(
    val id: Int,
    val especialidad: String,
    val servicio: String,
    val profesional: String,
    val fecha: String,
    val hora: String,
    val modalidad: String,
    val estado: String,
    val duracionMinutos: Int
)