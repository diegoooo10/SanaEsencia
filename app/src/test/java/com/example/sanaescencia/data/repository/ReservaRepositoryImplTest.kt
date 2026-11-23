package com.example.sanaescencia.data.repository

import com.example.sanaescencia.testutil.FakeReservaApi
import com.example.sanaescencia.testutil.TestData
import com.example.sanaescencia.testutil.responseHttpError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response
import java.io.IOException
import java.time.LocalDate

class ReservaRepositoryImplTest {

    private val api = FakeReservaApi()
    private val repository = ReservaRepositoryImpl(api)

    // --- Horarios disponibles ---

    @Test
    fun `convierte los horarios de texto a LocalTime y envia todos los parametros`() = runTest {
        api.horariosResponse = Response.success(listOf("09:00", "10:30:00"))

        val result = repository.obtenerHorariosDisponibles(
            token = "tk",
            profesionalId = 3,
            servicioId = 9,
            fecha = LocalDate.of(2026, 12, 1),
            modalidad = "Online"
        )

        assertEquals(
            listOf(java.time.LocalTime.of(9, 0), java.time.LocalTime.of(10, 30)),
            result.getOrNull()
        )
        assertEquals("Bearer tk", api.lastHorariosToken)
        assertEquals(3L, api.lastHorariosProfesionalId)
        assertEquals(9L, api.lastHorariosServicioId)
        assertEquals("2026-12-01", api.lastHorariosFecha)
        assertEquals("Online", api.lastHorariosModalidad)
    }

    @Test
    fun `horarios con cuerpo nulo devuelve failure`() = runTest {
        api.horariosResponse = Response.success(null)

        val result = repository.obtenerHorariosDisponibles(
            "tk", 1, 2, LocalDate.of(2026, 1, 1), "Presencial"
        )

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message.orEmpty().startsWith("El servidor devolvi"))
    }

    @Test
    fun `horarios con error HTTP devuelve failure`() = runTest {
        api.horariosResponse = responseHttpError(409)

        val result = repository.obtenerHorariosDisponibles(
            "tk", 1, 2, LocalDate.of(2026, 1, 1), "Presencial"
        )

        assertEquals("Error HTTP 409", result.exceptionOrNull()?.message)
    }

    @Test
    fun `horarios con formato invalido captura la excepcion de parseo`() = runTest {
        api.horariosResponse = Response.success(listOf("no-es-hora"))

        val result = repository.obtenerHorariosDisponibles(
            "tk", 1, 2, LocalDate.of(2026, 1, 1), "Presencial"
        )

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is java.time.format.DateTimeParseException)
    }

    @Test
    fun `horarios captura excepciones de red`() = runTest {
        api.error = IOException("red")

        val result = repository.obtenerHorariosDisponibles(
            "tk", 1, 2, LocalDate.of(2026, 1, 1), "Presencial"
        )

        assertTrue(result.exceptionOrNull() is IOException)
    }

    // --- Listado de reservas ---

    @Test
    fun `obtiene el listado de reservas`() = runTest {
        api.reservasResponse = Response.success(listOf(TestData.reserva))

        val result = repository.obtenerReservas("tk")

        assertEquals(listOf(TestData.reserva), result.getOrNull())
        assertEquals("Bearer tk", api.lastReservasToken)
    }

    @Test
    fun `listado con cuerpo nulo devuelve failure`() = runTest {
        api.reservasResponse = Response.success(null)

        val result = repository.obtenerReservas("tk")

        assertTrue(result.isFailure)
    }

    @Test
    fun `listado con error HTTP devuelve failure`() = runTest {
        api.reservasResponse = responseHttpError(401)

        val result = repository.obtenerReservas("tk")

        assertEquals("Error HTTP 401", result.exceptionOrNull()?.message)
    }

    @Test
    fun `listado captura excepciones`() = runTest {
        api.error = IllegalStateException("boom")

        val result = repository.obtenerReservas("tk")

        assertEquals("boom", result.exceptionOrNull()?.message)
    }

    // --- Detalle ---

    @Test
    fun `obtiene el detalle de una reserva por id`() = runTest {
        api.detalleResponse = Response.success(TestData.reserva)

        val result = repository.obtenerReservaPorId("tk", 100)

        assertEquals(TestData.reserva, result.getOrNull())
        assertEquals("Bearer tk", api.lastDetalleToken)
        assertEquals(100L, api.lastDetalleId)
    }

    @Test
    fun `detalle con cuerpo nulo devuelve failure`() = runTest {
        api.detalleResponse = Response.success(null)

        val result = repository.obtenerReservaPorId("tk", 100)

        assertTrue(result.isFailure)
    }

    @Test
    fun `detalle con error HTTP devuelve failure`() = runTest {
        api.detalleResponse = responseHttpError(404)

        val result = repository.obtenerReservaPorId("tk", 100)

        assertEquals("Error HTTP 404", result.exceptionOrNull()?.message)
    }

    @Test
    fun `detalle captura excepciones`() = runTest {
        api.error = RuntimeException("detalle")

        val result = repository.obtenerReservaPorId("tk", 100)

        assertEquals("detalle", result.exceptionOrNull()?.message)
    }

    // --- Creacion ---

    @Test
    fun `crea una reserva enviando el request`() = runTest {
        api.crearResponse = Response.success(TestData.reserva)

        val result = repository.crearReserva("tk", TestData.reservaRequest)

        assertEquals(TestData.reserva, result.getOrNull())
        assertEquals("Bearer tk", api.lastCrearToken)
        assertEquals(TestData.reservaRequest, api.lastCrearRequest)
    }

    @Test
    fun `creacion con cuerpo nulo devuelve failure`() = runTest {
        api.crearResponse = Response.success(null)

        val result = repository.crearReserva("tk", TestData.reservaRequest)

        assertTrue(result.isFailure)
    }

    @Test
    fun `creacion con error HTTP devuelve failure`() = runTest {
        api.crearResponse = responseHttpError(422)

        val result = repository.crearReserva("tk", TestData.reservaRequest)

        assertEquals("Error HTTP 422", result.exceptionOrNull()?.message)
    }

    @Test
    fun `creacion captura excepciones`() = runTest {
        api.error = IOException("crear")

        val result = repository.crearReserva("tk", TestData.reservaRequest)

        assertTrue(result.exceptionOrNull() is IOException)
    }

    // --- Cancelacion ---

    @Test
    fun `cancela una reserva exitosamente`() = runTest {
        api.cancelarResponse = Response.success(TestData.reserva)

        val result = repository.cancelarReserva("tk", 55)

        assertEquals(Unit, result.getOrNull())
        assertEquals("Bearer tk", api.lastCancelarToken)
        assertEquals(55L, api.lastCancelarId)
    }

    @Test
    fun `cancelacion con error HTTP devuelve failure`() = runTest {
        api.cancelarResponse = responseHttpError(400)

        val result = repository.cancelarReserva("tk", 55)

        assertEquals("Error HTTP 400", result.exceptionOrNull()?.message)
    }

    @Test
    fun `cancelacion captura excepciones`() = runTest {
        api.error = IOException("cancelar")

        val result = repository.cancelarReserva("tk", 55)

        assertTrue(result.exceptionOrNull() is IOException)
    }

    // --- Pago ---

    @Test
    fun `inicia el pago de una reserva`() = runTest {
        api.pagoResponse = Response.success(TestData.inicioPago)

        val result = repository.iniciarPago("tk", 100)

        assertEquals(TestData.inicioPago, result.getOrNull())
        assertEquals("Bearer tk", api.lastPagoToken)
        assertEquals(100L, api.lastPagoId)
    }

    @Test
    fun `pago con cuerpo nulo devuelve failure`() = runTest {
        api.pagoResponse = Response.success(null)

        val result = repository.iniciarPago("tk", 100)

        assertTrue(result.isFailure)
    }

    @Test
    fun `pago con error HTTP devuelve failure`() = runTest {
        api.pagoResponse = responseHttpError(500)

        val result = repository.iniciarPago("tk", 100)

        assertEquals("Error HTTP 500", result.exceptionOrNull()?.message)
    }

    @Test
    fun `pago captura excepciones`() = runTest {
        api.error = IOException("pago")

        val result = repository.iniciarPago("tk", 100)

        assertTrue(result.exceptionOrNull() is IOException)
    }

    // --- Cancelacion de corrutina ---

    @Test
    fun `propaga CancellationException en cada endpoint`() {
        api.error = CancellationException("cancelado")

        assertThrows(CancellationException::class.java) {
            runBlocking { repository.obtenerReservas("t") }
        }
        assertThrows(CancellationException::class.java) {
            runBlocking { repository.obtenerReservaPorId("t", 1) }
        }
        assertThrows(CancellationException::class.java) {
            runBlocking { repository.crearReserva("t", TestData.reservaRequest) }
        }
        assertThrows(CancellationException::class.java) {
            runBlocking { repository.cancelarReserva("t", 1) }
        }
        assertThrows(CancellationException::class.java) {
            runBlocking { repository.iniciarPago("t", 1) }
        }
        assertThrows(CancellationException::class.java) {
            runBlocking {
                repository.obtenerHorariosDisponibles(
                    "t", 1, 2, LocalDate.of(2026, 1, 1), "Online"
                )
            }
        }
    }
}
