
package com.example.sanaescencia.data.remote.dto

data class UsuarioResponse(
    val id: Long,
    val nombre: String,
    val apellido: String,
    val correo: String,
    val fechaNacimiento: String,
    val direccion: String?,
    val telefono: String?,
    val rol: String
)
