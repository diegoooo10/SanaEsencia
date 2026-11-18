
package com.example.sanaescencia.data.repository

import com.example.sanaescencia.data.remote.dto.LoginRequest
import com.example.sanaescencia.data.remote.dto.LoginResponse
import com.example.sanaescencia.data.remote.dto.UsuarioRequest
import com.example.sanaescencia.data.remote.dto.UsuarioResponse
import com.example.sanaescencia.data.remote.dto.UsuarioUpdateRequest
import com.example.sanaescencia.data.remote.dto.RecuperacionPasswordRequest
import com.example.sanaescencia.data.remote.dto.RestablecerPasswordRequest

interface UsuarioRepository {

    suspend fun registrar(
        request: UsuarioRequest
    ): Result<UsuarioResponse>

    suspend fun iniciarSesion(
        request: LoginRequest
    ): Result<LoginResponse>

    suspend fun obtenerPerfil(
        token: String
    ): Result<UsuarioResponse>

    suspend fun actualizarPerfil(
        token: String,
        request: UsuarioUpdateRequest
    ): Result<UsuarioResponse>


    suspend fun solicitarRecuperacion(
        request: RecuperacionPasswordRequest
    ): Result<Unit>

    suspend fun restablecerPassword(
        request: RestablecerPasswordRequest
    ): Result<Unit>

}
