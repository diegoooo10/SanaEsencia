
package com.example.sanaescencia.data.remote.dto

data class InicioPagoResponse(
    val pago: PagoResponse,
    val url: String,
    val token: String
)

data class PagoResponse(
    val id: Long,
    val reservaId: Long,
    val usuarioId: Long,
    val monto: Int,
    val estado: String,
    val ordenCompra: String
)
