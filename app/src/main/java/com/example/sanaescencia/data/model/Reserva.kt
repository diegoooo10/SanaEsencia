package com.example.sanaescencia.data.model

import java.time.LocalDate
import java.time.LocalTime

data class Reserva(
    val id: Int,
    val especialidad: String,
    val servicio: String,
    val profesional: String,
    val fecha: LocalDate,
    val hora: LocalTime,
    val modalidad: String,
    val estado: String,
    val duracionMinutos: Int
)