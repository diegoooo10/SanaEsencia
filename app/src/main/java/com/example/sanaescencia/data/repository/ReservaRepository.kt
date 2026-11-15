package com.example.sanaescencia.data.repository

import com.example.sanaescencia.data.model.Reserva

interface ReservaRepository {

    suspend fun obtenerReservas(): Result<List<Reserva>>

    suspend fun obtenerReservaPorId(id: Int): Result<Reserva>

    suspend fun crearReserva(reserva: Reserva): Result<Reserva>

    suspend fun cancelarReserva(id: Int): Result<Unit>
}