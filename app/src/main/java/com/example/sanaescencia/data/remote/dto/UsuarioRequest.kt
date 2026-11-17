
package com.example.sanaescencia.data.remote.dto

data class UsuarioRequest(
    val nombre: String,
    val apellido: String,
    val correo: String,
    val fechaNacimiento: String,
    val password: String,
    val direccion: String?,
    val telefono: String?
)
