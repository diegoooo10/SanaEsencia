package com.example.sanaescencia.testutil

import com.example.sanaescencia.data.model.Especialidad
import com.example.sanaescencia.data.model.Profesional
import com.example.sanaescencia.data.model.Reserva
import com.example.sanaescencia.data.model.Servicio
import com.example.sanaescencia.data.remote.dto.InicioPagoResponse
import com.example.sanaescencia.data.remote.dto.LoginRequest
import com.example.sanaescencia.data.remote.dto.LoginResponse
import com.example.sanaescencia.data.remote.dto.PagoResponse
import com.example.sanaescencia.data.remote.dto.RecuperacionPasswordRequest
import com.example.sanaescencia.data.remote.dto.ReservaRequest
import com.example.sanaescencia.data.remote.dto.RestablecerPasswordRequest
import com.example.sanaescencia.data.remote.dto.UsuarioRequest
import com.example.sanaescencia.data.remote.dto.UsuarioResponse
import com.example.sanaescencia.data.remote.dto.UsuarioUpdateRequest
import java.time.LocalDate
import java.time.LocalTime

/**
 * Fixtures compartidos. Las fechas de los modelos son fijas porque no dependen
 * del reloj; las pruebas de UI construyen sus propias fechas relativas a hoy.
 */
object TestData {

    val usuarioResponse = UsuarioResponse(
        id = 1L,
        nombre = "Ana",
        apellido = "Pérez",
        correo = "ana@test.cl",
        fechaNacimiento = "1990-05-10",
        direccion = "Av. Siempre Viva 123",
        telefono = "+56 9 12345678",
        rol = "CLIENTE"
    )

    val loginResponse = LoginResponse(
        token = "jwt-token",
        tipo = "Bearer",
        correo = "ana@test.cl",
        rol = "CLIENTE",
        expiracionMs = 3_600_000L
    )

    val usuarioRequest = UsuarioRequest(
        nombre = "Ana",
        apellido = "Pérez",
        correo = "ana@test.cl",
        fechaNacimiento = "1990-05-10",
        password = "password123",
        direccion = "Av. Siempre Viva 123",
        telefono = "+56 9 12345678"
    )

    val usuarioUpdateRequest = UsuarioUpdateRequest(
        correo = "ana@test.cl",
        direccion = "Av. Nueva 456",
        telefono = "+56 9 87654321"
    )

    val loginRequest = LoginRequest(correo = "ana@test.cl", password = "password123")

    val recuperacionRequest = RecuperacionPasswordRequest(correo = "ana@test.cl")

    val restablecerRequest = RestablecerPasswordRequest(
        token = "reset-token",
        nuevaPassword = "nuevaPassword123"
    )

    val especialidad = Especialidad(
        id = 1,
        nombre = "Psicología",
        descripcion = "Atención psicológica integral"
    )

    val servicio = Servicio(
        id = 10,
        nombre = "Terapia individual",
        especialidadId = 1,
        duracionMinutos = 60
    )

    val profesional = Profesional(
        id = 5,
        nombre = "Dra. Ana Soto",
        tituloProfesional = "Psicóloga clínica",
        especialidadesIds = listOf(1, 2),
        modalidades = listOf("Presencial", "Online"),
        estadoAgenda = "DISPONIBLE"
    )

    val reserva = Reserva(
        id = 100L,
        profesionalId = 5L,
        servicioId = 10L,
        especialidad = "Psicología",
        servicio = "Terapia individual",
        profesional = "Dra. Ana Soto",
        fecha = LocalDate.of(2026, 12, 1),
        hora = LocalTime.of(10, 0),
        modalidad = "Presencial",
        estado = "Confirmada",
        duracionMinutos = 60
    )

    val inicioPago = InicioPagoResponse(
        pago = PagoResponse(
            id = 1L,
            reservaId = 100L,
            usuarioId = 1L,
            monto = 25_000,
            estado = "PENDIENTE",
            ordenCompra = "OC-100"
        ),
        url = "https://webpay.test/pagar",
        token = "token-ws"
    )

    val reservaRequest = ReservaRequest(
        profesionalId = 5L,
        servicioId = 10L,
        fecha = "2026-12-01",
        hora = "10:00",
        modalidad = "Presencial"
    )
}
