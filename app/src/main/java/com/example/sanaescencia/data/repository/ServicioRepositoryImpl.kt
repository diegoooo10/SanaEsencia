
package com.example.sanaescencia.data.repository

import com.example.sanaescencia.data.model.Especialidad
import com.example.sanaescencia.data.model.Servicio
import com.example.sanaescencia.data.remote.api.ServicioApiService
import kotlinx.coroutines.CancellationException

class ServicioRepositoryImpl(
    private val api: ServicioApiService
) : ServicioRepository {

    override suspend fun obtenerEspecialidades(
        token: String
    ): Result<List<Especialidad>> {
        return try {
            val response = api.obtenerEspecialidades("Bearer $token")

            if (response.isSuccessful) {
                val body = response.body()

                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(
                        Exception("El servidor devolvio una respuesta vacia")
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

    override suspend fun obtenerServicios(
        token: String,
        especialidadId: Int?
    ): Result<List<Servicio>> {
        return try {
            val response = api.obtenerServicios(
                token = "Bearer $token",
                especialidadId = especialidadId
            )

            if (response.isSuccessful) {
                val body = response.body()

                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(
                        Exception("El servidor devolvio una respuesta vacia")
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
}
