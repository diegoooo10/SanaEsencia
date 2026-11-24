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
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate
import java.time.LocalTime

class DtoSerializationTest : FunSpec({

    val gson: Gson = GsonBuilder()
        .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
        .registerTypeAdapter(LocalTime::class.java, LocalTimeAdapter())
        .create()

    test("login request conserva correo y password") {
        val json = gson.toJson(TestData.loginRequest)
        val recuperado = gson.fromJson(json, LoginRequest::class.java)

        recuperado shouldBe TestData.loginRequest
    }

    test("login response expone todos sus campos") {
        val json = gson.toJson(TestData.loginResponse)
        val recuperado = gson.fromJson(json, LoginResponse::class.java)

        recuperado.token shouldBe "jwt-token"
        recuperado.tipo shouldBe "Bearer"
        recuperado.correo shouldBe "ana@test.cl"
        recuperado.rol shouldBe "CLIENTE"
        recuperado.expiracionMs shouldBe 3_600_000L
    }

    test("usuario request con direccion y telefono nulos los omite") {
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

        json.contains("direccion") shouldBe false
        json.contains("telefono") shouldBe false
        gson.fromJson(json, UsuarioRequest::class.java) shouldBe request
    }

    test("usuario update request usa valores por defecto nulos") {
        val request = UsuarioUpdateRequest()

        request.correo shouldBe null
        request.direccion shouldBe null
        request.telefono shouldBe null
    }

    test("usuario update request solo envia campos no nulos") {
        val request = UsuarioUpdateRequest(correo = "nuevo@test.cl")

        val json = gson.toJson(request)

        json.contains("nuevo@test.cl") shouldBe true
        json.contains("direccion") shouldBe false
        json.contains("telefono") shouldBe false
    }

    test("usuario response deserializa campos opcionales") {
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

        response.id shouldBe 7L
        response.direccion shouldBe null
        response.telefono shouldBe null
    }

    test("reserva request se serializa con fecha y hora como texto") {
        val json = gson.toJson(TestData.reservaRequest)
        val recuperado = gson.fromJson(json, ReservaRequest::class.java)

        json.contains("2026-12-01") shouldBe true
        json.contains("10:00") shouldBe true
        recuperado shouldBe TestData.reservaRequest
    }

    test("recuperacion y restablecimiento conservan sus datos") {
        val recuperacion = gson.fromJson(
            gson.toJson(TestData.recuperacionRequest),
            RecuperacionPasswordRequest::class.java
        )
        val restablecer = gson.fromJson(
            gson.toJson(TestData.restablecerRequest),
            RestablecerPasswordRequest::class.java
        )

        recuperacion.correo shouldBe "ana@test.cl"
        restablecer.token shouldBe "reset-token"
        restablecer.nuevaPassword shouldBe "nuevaPassword123"
    }

    test("inicio de pago anida el pago y conserva la url") {
        val json = gson.toJson(TestData.inicioPago)
        val recuperado = gson.fromJson(json, InicioPagoResponse::class.java)

        recuperado shouldBe TestData.inicioPago
        recuperado.pago.monto shouldBe 25_000
        recuperado.pago.ordenCompra shouldBe "OC-100"
    }

    test("modelo reserva serializa fechas con los adaptadores") {
        val json = gson.toJson(TestData.reserva)
        val recuperada = gson.fromJson(json, Reserva::class.java)

        recuperada shouldBe TestData.reserva
        recuperada.fecha shouldBe LocalDate.of(2026, 12, 1)
        recuperada.hora shouldBe LocalTime.of(10, 0)
    }

    test("modelos de catalogo hacen round trip") {
        val especialidad = gson.fromJson(
            gson.toJson(TestData.especialidad),
            Especialidad::class.java
        )
        val servicio = gson.fromJson(gson.toJson(TestData.servicio), Servicio::class.java)
        val profesional = gson.fromJson(
            gson.toJson(TestData.profesional),
            Profesional::class.java
        )
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

        especialidad shouldBe TestData.especialidad
        servicio shouldBe TestData.servicio
        profesional shouldBe TestData.profesional
        usuarioRecuperado shouldBe usuario
    }

    test("usuario modelo acepta ciudad nula") {
        val usuario = Usuario(
            id = 1L,
            nombre = "Ana",
            apellido = "Pérez",
            telefono = "123",
            fechaNacimiento = LocalDate.of(1990, 5, 10),
            ciudad = null,
            correo = "ana@test.cl"
        )

        usuario.copy().ciudad shouldBe null
        usuario.copy() shouldBe usuario
    }
})
