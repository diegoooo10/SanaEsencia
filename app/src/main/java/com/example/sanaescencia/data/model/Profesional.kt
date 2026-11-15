package com.example.sanaescencia.data.model

data class Profesional(
    val id: Int,
    val nombre: String,
    val especialidadesIds: List<Int>
)