
package com.example.sanaescencia.data.repository

import com.example.sanaescencia.data.model.Profesional
import com.example.sanaescencia.data.remote.api.ProfesionalApiService
import kotlinx.coroutines.CancellationException

class ProfesionalRepositoryImpl(
    private val api: ProfesionalApiService
) : ProfesionalRepository {

    override suspend fun obtenerProfesionales(
        token: String
    ): Result<List<Profesional>> {
        return try {
            val response = api.obtenerProfesionales("Bearer $token")

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
