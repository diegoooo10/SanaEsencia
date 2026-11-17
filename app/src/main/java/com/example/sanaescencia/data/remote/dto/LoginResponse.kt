
package com.example.sanaescencia.data.remote.dto

data class LoginResponse(
    val token: String,
    val tipo: String,
    val correo: String,
    val rol: String,
    val expiracionMs: Long
)
