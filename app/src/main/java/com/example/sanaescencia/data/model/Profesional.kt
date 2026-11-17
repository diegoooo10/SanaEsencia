
package com.example.sanaescencia.data.model

data class Profesional(
    val id: Int,
    val nombre: String,
    val tituloProfesional: String,
    val especialidadesIds: List<Int>,
    val modalidades: List<String>,
    val estadoAgenda: String
)
