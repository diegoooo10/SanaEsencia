
package com.example.sanaescencia.data.repository

import com.example.sanaescencia.data.model.Reserva
import com.example.sanaescencia.data.remote.api.ReservaApiService
import kotlinx.coroutines.CancellationException
import com.example.sanaescencia.data.remote.dto.ReservaRequest
import java.time.LocalDate
import java.time.LocalTime

class ReservaRepositoryImpl(
    private val api: ReservaApiService
) : ReservaRepository {

    override suspend fun obtenerHorariosDisponibles(
        token: String,
        profesionalId: Long,
        servicioId: Long,
        fecha: LocalDate,
        modalidad: String
    ): Result<List<LocalTime>> {

        return try {
            val response = api.obtenerHorariosDisponibles(
                token = "Bearer $token",
                profesionalId = profesionalId,
                servicioId = servicioId,
                fecha = fecha.toString(),
                modalidad = modalidad
            )

            if (response.isSuccessful) {
                val body = response.body()

                if (body != null) {
                    Result.success(
                        body.map { LocalTime.parse(it) }
                    )
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

    override suspend fun obtenerReservas(
        token: String
    ): Result<List<Reserva>> {

        return try {
            val response = api.obtenerReservas("Bearer $token")

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

    override suspend fun obtenerReservaPorId(
        token: String,
        id: Long
    ): Result<Reserva> {

        return try {
            val response = api.obtenerReservaPorId(
                token = "Bearer $token",
                id = id
            )

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

    override suspend fun crearReserva(
        token: String,
        request: ReservaRequest
    ): Result<Reserva> {

        return try {
            val response = api.crearReserva(
                token = "Bearer $token",
                reserva = request
            )

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

    override suspend fun cancelarReserva(
        token: String,
        id: Long
    ): Result<Unit> {

        return try {
            val response = api.cancelarReserva(
                token = "Bearer $token",
                id = id
            )

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
