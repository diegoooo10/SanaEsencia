package com.example.sanaescencia.data.model

data class Servicio(
    val id: Int,
    val nombre: String,
    val especialidadId: Int,
    val profesionalesIds: List<Int>,
    val duracionMinutos: Int
)