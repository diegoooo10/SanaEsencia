package com.example.sanaescencia.data.remote.dto

import com.example.sanaescencia.data.model.Especialidad
import com.example.sanaescencia.data.model.Profesional
import com.example.sanaescencia.data.model.Reserva
import com.example.sanaescencia.data.model.Servicio
import com.example.sanaescencia.data.model.Usuario
import com.example.sanaescencia.data.remote.adapter.LocalDateAdapter
import com.example.sanaescencia.data.remote.adapter.LocalTimeAdapter
import com.example.sanaescencia.testutil.TestData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class DtoSerializationTest {

    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
        .registerTypeAdapter(LocalTime::class.java, LocalTimeAdapter())
        .create()

    @Test
    fun `login request conserva correo y password`() {
        val json = gson.toJson(TestData.loginRequest)
        val recuperado = gson.fromJson(json, LoginRequest::class.java)

        assertEquals(TestData.loginRequest, recuperado)
    }

    @Test
    fun `login response expone todos sus campos`() {
        val json = gson.toJson(TestData.loginResponse)
        val recuperado = gson.fromJson(json, LoginResponse::class.java)

        assertEquals("jwt-token", recuperado.token)
        assertEquals("Bearer", recuperado.tipo)
        assertEquals("ana@test.cl", recuperado.correo)
        assertEquals("CLIENTE", recuperado.rol)
        assertEquals(3_600_000L, recuperado.expiracionMs)
    }

    @Test
    fun `usuario request con direccion y telefono nulos los omite`() {
        val request = UsuarioRequest(
            nombre = "Ana",
            apellido = "Pérez",
            correo = "ana@test.cl",
            fechaNacimiento = "1990-05-10",
            password = "password123",
            direccion = null,
            telefono = null
        )

        val json = gson.toJson(request)

        assertFalse(json.contains("direccion"))
        assertFalse(json.contains("telefono"))
        assertEquals(request, gson.fromJson(json, UsuarioRequest::class.java))
    }

    @Test
    fun `usuario update request usa valores por defecto nulos`() {
        val request = UsuarioUpdateRequest()

        assertNull(request.correo)
        assertNull(request.direccion)
        assertNull(request.telefono)
    }

    @Test
    fun `usuario update request solo envia campos no nulos`() {
        val request = UsuarioUpdateRequest(correo = "nuevo@test.cl")

        val json = gson.toJson(request)

        assertTrue(json.contains("nuevo@test.cl"))
        assertFalse(json.contains("direccion"))
        assertFalse(json.contains("telefono"))
    }

    @Test
    fun `usuario response deserializa campos opcionales`() {
        val json = """
            {
              "id": 7,
              "nombre": "Ana",
              "apellido": "Pérez",
              "correo": "ana@test.cl",
              "fechaNacimiento": "1990-05-10",
              "rol": "CLIENTE"
            }
        """.trimIndent()

        val response = gson.fromJson(json, UsuarioResponse::class.java)

        assertEquals(7L, response.id)
        assertNull(response.direccion)
        assertNull(response.telefono)
    }

    @Test
    fun `reserva request se serializa con fecha y hora como texto`() {
        val json = gson.toJson(TestData.reservaRequest)
        val recuperado = gson.fromJson(json, ReservaRequest::class.java)

        assertTrue(json.contains("2026-12-01"))
        assertTrue(json.contains("10:00"))
        assertEquals(TestData.reservaRequest, recuperado)
    }

    @Test
    fun `recuperacion y restablecimiento conservan sus datos`() {
        val recuperacion = gson.fromJson(
            gson.toJson(TestData.recuperacionRequest),
            RecuperacionPasswordRequest::class.java
        )
        val restablecer = gson.fromJson(
            gson.toJson(TestData.restablecerRequest),
            RestablecerPasswordRequest::class.java
        )

        assertEquals("ana@test.cl", recuperacion.correo)
        assertEquals("reset-token", restablecer.token)
        assertEquals("nuevaPassword123", restablecer.nuevaPassword)
    }

    @Test
    fun `inicio de pago anida el pago y conserva la url`() {
        val json = gson.toJson(TestData.inicioPago)
        val recuperado = gson.fromJson(json, InicioPagoResponse::class.java)

        assertEquals(TestData.inicioPago, recuperado)
        assertEquals(25_000, recuperado.pago.monto)
        assertEquals("OC-100", recuperado.pago.ordenCompra)
    }

    @Test
    fun `modelo reserva serializa fechas con los adaptadores`() {
        val json = gson.toJson(TestData.reserva)
        val recuperada = gson.fromJson(json, Reserva::class.java)

        assertEquals(TestData.reserva, recuperada)
        assertEquals(LocalDate.of(2026, 12, 1), recuperada.fecha)
        assertEquals(LocalTime.of(10, 0), recuperada.hora)
    }

    @Test
    fun `modelos de catalogo hacen round trip`() {
        val especialidad = gson.fromJson(gson.toJson(TestData.especialidad), Especialidad::class.java)
        val servicio = gson.fromJson(gson.toJson(TestData.servicio), Servicio::class.java)
        val profesional = gson.fromJson(gson.toJson(TestData.profesional), Profesional::class.java)
        val usuario = Usuario(
            id = 1L,
            nombre = "Ana",
            apellido = "Pérez",
            telefono = "+56 9 12345678",
            fechaNacimiento = LocalDate.of(1990, 5, 10),
            ciudad = "Santiago",
            correo = "ana@test.cl"
        )
        val usuarioRecuperado = gson.fromJson(gson.toJson(usuario), Usuario::class.java)

        assertEquals(TestData.especialidad, especialidad)
        assertEquals(TestData.servicio, servicio)
        assertEquals(TestData.profesional, profesional)
        assertEquals(usuario, usuarioRecuperado)
    }

    @Test
    fun `usuario modelo acepta ciudad nula`() {
        val usuario = Usuario(
            id = 1L,
            nombre = "Ana",
            apellido = "Pérez",
            telefono = "123",
            fechaNacimiento = LocalDate.of(1990, 5, 10),
            ciudad = null,
            correo = "ana@test.cl"
        )

        assertNull(usuario.copy().ciudad)
        assertEquals(usuario, usuario.copy())
    }
}
