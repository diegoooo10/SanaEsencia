
package com.example.sanaescencia.data.repository

import com.example.sanaescencia.data.remote.api.UsuarioApiService
import com.example.sanaescencia.data.remote.dto.LoginRequest
import com.example.sanaescencia.data.remote.dto.LoginResponse
import com.example.sanaescencia.data.remote.dto.UsuarioRequest
import com.example.sanaescencia.data.remote.dto.UsuarioResponse
import com.example.sanaescencia.data.remote.dto.UsuarioUpdateRequest
import com.example.sanaescencia.data.remote.dto.RecuperacionPasswordRequest
import com.example.sanaescencia.data.remote.dto.RestablecerPasswordRequest

import kotlinx.coroutines.CancellationException
import retrofit2.Response

class UsuarioRepositoryImpl(
    private val api: UsuarioApiService
) : UsuarioRepository {

    private suspend fun <T : Any> ejecutar(
        solicitud: suspend () -> Response<T>
    ): Result<T> {
        return try {
            val response = solicitud()

            if (response.isSuccessful) {
                val body = response.body()

                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(
                        Exception("El servidor devolvió una respuesta vacía")
                    )
                }
            } else {
                Result.failure(
                    Exception("Error HTTP ${response.code()}")
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun registrar(
        request: UsuarioRequest
    ): Result<UsuarioResponse> =
        ejecutar { api.registrar(request) }

    override suspend fun iniciarSesion(
        request: LoginRequest
    ): Result<LoginResponse> =
        ejecutar { api.iniciarSesion(request) }

    override suspend fun obtenerPerfil(
        token: String
    ): Result<UsuarioResponse> =
        ejecutar { api.obtenerPerfil("Bearer $token") }

    override suspend fun actualizarPerfil(
        token: String,
        request: UsuarioUpdateRequest
    ): Result<UsuarioResponse> =
        ejecutar { api.actualizarPerfil("Bearer $token", request) }


    override suspend fun solicitarRecuperacion(
        request: RecuperacionPasswordRequest
    ): Result<Unit> {
        return try {
            val response = api.solicitarRecuperacion(request)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(
                    Exception("Error HTTP ${response.code()}")
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun restablecerPassword(
        request: RestablecerPasswordRequest
    ): Result<Unit> {
        return try {
            val response = api.restablecerPassword(request)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(
                    Exception("Error HTTP ${response.code()}")
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
