
package com.example.sanaescencia.data.remote.api

import com.example.sanaescencia.data.remote.dto.LoginRequest
import com.example.sanaescencia.data.remote.dto.LoginResponse
import com.example.sanaescencia.data.remote.dto.UsuarioRequest
import com.example.sanaescencia.data.remote.dto.UsuarioResponse
import com.example.sanaescencia.data.remote.dto.UsuarioUpdateRequest
import com.example.sanaescencia.data.remote.dto.RecuperacionPasswordRequest
import com.example.sanaescencia.data.remote.dto.RestablecerPasswordRequest

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST

interface UsuarioApiService {

    @POST("api/usuarios/registro")
    suspend fun registrar(
        @Body request: UsuarioRequest
    ): Response<UsuarioResponse>

    @POST("api/usuarios/login")
    suspend fun iniciarSesion(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("api/usuarios/password/recuperacion")
    suspend fun solicitarRecuperacion(
        @Body request: RecuperacionPasswordRequest
    ): Response<okhttp3.ResponseBody>

    @GET("api/usuarios/perfil")
    suspend fun obtenerPerfil(
        @Header("Authorization") token: String
    ): Response<UsuarioResponse>

    @PATCH("api/usuarios/perfil")
    suspend fun actualizarPerfil(
        @Header("Authorization") token: String,
        @Body request: UsuarioUpdateRequest
    ): Response<UsuarioResponse>

    @POST("api/usuarios/password/restablecer")
    suspend fun restablecerPassword(
        @Body request: RestablecerPasswordRequest
    ): Response<okhttp3.ResponseBody>
}
