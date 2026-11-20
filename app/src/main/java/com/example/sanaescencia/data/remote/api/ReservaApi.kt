
package com.example.sanaescencia.data.remote.api

import com.example.sanaescencia.data.model.Reserva
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import com.example.sanaescencia.data.remote.dto.ReservaRequest
import com.example.sanaescencia.data.remote.dto.InicioPagoResponse

interface ReservaApiService {

    @GET("api/reservas/disponibilidad")
    suspend fun obtenerHorariosDisponibles(
        @Header("Authorization") token: String,
        @Query("profesionalId") profesionalId: Long,
        @Query("servicioId") servicioId: Long,
        @Query("fecha") fecha: String,
        @Query("modalidad") modalidad: String
    ): Response<List<String>>

    @POST("api/reservas")
    suspend fun crearReserva(
        @Header("Authorization") token: String,
        @Body reserva: ReservaRequest
    ): Response<Reserva>

    @GET("api/reservas")
    suspend fun obtenerReservas(
        @Header("Authorization") token: String
    ): Response<List<Reserva>>

    @GET("api/reservas/{id}")
    suspend fun obtenerReservaPorId(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<Reserva>

    @PATCH("api/reservas/{id}/cancelacion")
    suspend fun cancelarReserva(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<Reserva>


    @POST("api/reservas/{id}/pago")
    suspend fun iniciarPago(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<InicioPagoResponse>

}
