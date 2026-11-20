
package com.example.sanaescencia.data.repository

import com.example.sanaescencia.data.model.Reserva
import com.example.sanaescencia.data.remote.dto.ReservaRequest
import com.example.sanaescencia.data.remote.dto.InicioPagoResponse
import java.time.LocalDate
import java.time.LocalTime

interface ReservaRepository {

    suspend fun obtenerHorariosDisponibles(
        token: String,
        profesionalId: Long,
        servicioId: Long,
        fecha: LocalDate,
        modalidad: String
    ): Result<List<LocalTime>>

    suspend fun obtenerReservas(
        token: String
    ): Result<List<Reserva>>

    suspend fun obtenerReservaPorId(
        token: String,
        id: Long
    ): Result<Reserva>

    suspend fun crearReserva(
        token: String,
        request: ReservaRequest
    ): Result<Reserva>

    suspend fun cancelarReserva(
        token: String,
        id: Long
    ): Result<Unit>

    suspend fun iniciarPago(
        token: String,
        reservaId: Long
    ): Result<InicioPagoResponse>
}
