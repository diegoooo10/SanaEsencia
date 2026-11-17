
package com.example.sanaescencia.data.model

import java.time.LocalDate

data class Usuario(
    val id: Long,
    val nombre: String,
    val apellido: String,
    val telefono: String,
    val fechaNacimiento: LocalDate,
    val ciudad: String?,
    val correo: String
)
