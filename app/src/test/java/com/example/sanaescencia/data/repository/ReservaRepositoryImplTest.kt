package com.example.sanaescencia.data.repository

import com.example.sanaescencia.data.model.Reserva
import com.example.sanaescencia.data.remote.api.ReservaApiService
import com.example.sanaescencia.testutil.TestData
import com.example.sanaescencia.testutil.responseHttpError
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import retrofit2.Response
import java.io.IOException
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeParseException

class ReservaRepositoryImplTest : FunSpec({

    lateinit var api: ReservaApiService
    lateinit var repository: ReservaRepositoryImpl

    beforeTest {
        api = mockk()
        repository = ReservaRepositoryImpl(api)
    }

    // --- Horarios disponibles ---

    test("convierte los horarios de texto a LocalTime y envia todos los parametros") {
        runTest {
            coEvery {
                api.obtenerHorariosDisponibles("Bearer tk", 3L, 9L, "2026-12-01", "Online")
            } returns Response.success(listOf("09:00", "10:30:00"))

            val result = repository.obtenerHorariosDisponibles(
                token = "tk",
                profesionalId = 3,
                servicioId = 9,
                fecha = LocalDate.of(2026, 12, 1),
                modalidad = "Online"
            )

            result.getOrNull() shouldBe listOf(LocalTime.of(9, 0), LocalTime.of(10, 30))
            coVerify(exactly = 1) {
                api.obtenerHorariosDisponibles("Bearer tk", 3L, 9L, "2026-12-01", "Online")
            }
        }
    }

    test("horarios con cuerpo nulo devuelve failure") {
        runTest {
            coEvery { api.obtenerHorariosDisponibles(any(), any(), any(), any(), any()) } returns
                Response.success<List<String>>(null)

            val result = repository.obtenerHorariosDisponibles(
                "tk", 1, 2, LocalDate.of(2026, 1, 1), "Presencial"
            )

            result.isFailure shouldBe true
            result.exceptionOrNull()?.message.orEmpty().startsWith("El servidor devolvi") shouldBe true
        }
    }

    test("horarios con error HTTP devuelve failure") {
        runTest {
            coEvery { api.obtenerHorariosDisponibles(any(), any(), any(), any(), any()) } returns
                responseHttpError(409)

            val result = repository.obtenerHorariosDisponibles(
                "tk", 1, 2, LocalDate.of(2026, 1, 1), "Presencial"
            )

            result.exceptionOrNull()?.message shouldBe "Error HTTP 409"
        }
    }

    test("horarios con formato invalido captura la excepcion de parseo") {
        runTest {
            coEvery { api.obtenerHorariosDisponibles(any(), any(), any(), any(), any()) } returns
                Response.success(listOf("no-es-hora"))

            val result = repository.obtenerHorariosDisponibles(
                "tk", 1, 2, LocalDate.of(2026, 1, 1), "Presencial"
            )

            result.isFailure shouldBe true
            result.exceptionOrNull().shouldBeInstanceOf<DateTimeParseException>()
        }
    }

    test("horarios captura excepciones de red") {
        runTest {
            coEvery { api.obtenerHorariosDisponibles(any(), any(), any(), any(), any()) } throws
                IOException("red")

            val result = repository.obtenerHorariosDisponibles(
                "tk", 1, 2, LocalDate.of(2026, 1, 1), "Presencial"
            )

            result.exceptionOrNull().shouldBeInstanceOf<IOException>()
        }
    }

    // --- Listado de reservas ---

    test("obtiene el listado de reservas") {
        runTest {
            coEvery { api.obtenerReservas("Bearer tk") } returns
                Response.success(listOf(TestData.reserva))

            val result = repository.obtenerReservas("tk")

            result.getOrNull() shouldBe listOf(TestData.reserva)
            coVerify(exactly = 1) { api.obtenerReservas("Bearer tk") }
        }
    }

    test("listado con cuerpo nulo devuelve failure") {
        runTest {
            coEvery { api.obtenerReservas(any()) } returns Response.success<List<Reserva>>(null)

            val result = repository.obtenerReservas("tk")

            result.isFailure shouldBe true
        }
    }

    test("listado con error HTTP devuelve failure") {
        runTest {
            coEvery { api.obtenerReservas(any()) } returns responseHttpError(401)

            val result = repository.obtenerReservas("tk")

            result.exceptionOrNull()?.message shouldBe "Error HTTP 401"
        }
    }

    test("listado captura excepciones") {
        runTest {
            coEvery { api.obtenerReservas(any()) } throws IllegalStateException("boom")

            val result = repository.obtenerReservas("tk")

            result.exceptionOrNull()?.message shouldBe "boom"
        }
    }

    // --- Detalle ---

    test("obtiene el detalle de una reserva por id") {
        runTest {
            coEvery { api.obtenerReservaPorId("Bearer tk", 100L) } returns
                Response.success(TestData.reserva)

            val result = repository.obtenerReservaPorId("tk", 100)

            result.getOrNull() shouldBe TestData.reserva
            coVerify(exactly = 1) { api.obtenerReservaPorId("Bearer tk", 100L) }
        }
    }

    test("detalle con cuerpo nulo devuelve failure") {
        runTest {
            coEvery { api.obtenerReservaPorId(any(), any()) } returns
                Response.success<Reserva>(null)

            val result = repository.obtenerReservaPorId("tk", 100)

            result.isFailure shouldBe true
        }
    }

    test("detalle con error HTTP devuelve failure") {
        runTest {
            coEvery { api.obtenerReservaPorId(any(), any()) } returns responseHttpError(404)

            val result = repository.obtenerReservaPorId("tk", 100)

            result.exceptionOrNull()?.message shouldBe "Error HTTP 404"
        }
    }

    test("detalle captura excepciones") {
        runTest {
            coEvery { api.obtenerReservaPorId(any(), any()) } throws
                RuntimeException("detalle")

            val result = repository.obtenerReservaPorId("tk", 100)

            result.exceptionOrNull()?.message shouldBe "detalle"
        }
    }

    // --- Creacion ---

    test("crea una reserva enviando el request") {
        runTest {
            coEvery { api.crearReserva("Bearer tk", TestData.reservaRequest) } returns
                Response.success(TestData.reserva)

            val result = repository.crearReserva("tk", TestData.reservaRequest)

            result.getOrNull() shouldBe TestData.reserva
            coVerify(exactly = 1) { api.crearReserva("Bearer tk", TestData.reservaRequest) }
        }
    }

    test("creacion con cuerpo nulo devuelve failure") {
        runTest {
            coEvery { api.crearReserva(any(), any()) } returns Response.success<Reserva>(null)

            val result = repository.crearReserva("tk", TestData.reservaRequest)

            result.isFailure shouldBe true
        }
    }

    test("creacion con error HTTP devuelve failure") {
        runTest {
            coEvery { api.crearReserva(any(), any()) } returns responseHttpError(422)

            val result = repository.crearReserva("tk", TestData.reservaRequest)

            result.exceptionOrNull()?.message shouldBe "Error HTTP 422"
        }
    }

    test("creacion captura excepciones") {
        runTest {
            coEvery { api.crearReserva(any(), any()) } throws IOException("crear")

            val result = repository.crearReserva("tk", TestData.reservaRequest)

            result.exceptionOrNull().shouldBeInstanceOf<IOException>()
        }
    }

    // --- Cancelacion ---

    test("cancela una reserva exitosamente") {
        runTest {
            coEvery { api.cancelarReserva("Bearer tk", 55L) } returns
                Response.success(TestData.reserva)

            val result = repository.cancelarReserva("tk", 55)

            result.getOrNull() shouldBe Unit
            coVerify(exactly = 1) { api.cancelarReserva("Bearer tk", 55L) }
        }
    }

    test("cancelacion con error HTTP devuelve failure") {
        runTest {
            coEvery { api.cancelarReserva(any(), any()) } returns responseHttpError(400)

            val result = repository.cancelarReserva("tk", 55)

            result.exceptionOrNull()?.message shouldBe "Error HTTP 400"
        }
    }

    test("cancelacion captura excepciones") {
        runTest {
            coEvery { api.cancelarReserva(any(), any()) } throws IOException("cancelar")

            val result = repository.cancelarReserva("tk", 55)

            result.exceptionOrNull().shouldBeInstanceOf<IOException>()
        }
    }

    // --- Pago ---

    test("inicia el pago de una reserva") {
        runTest {
            coEvery { api.iniciarPago("Bearer tk", 100L) } returns
                Response.success(TestData.inicioPago)

            val result = repository.iniciarPago("tk", 100)

            result.getOrNull() shouldBe TestData.inicioPago
            coVerify(exactly = 1) { api.iniciarPago("Bearer tk", 100L) }
        }
    }

    test("pago con cuerpo nulo devuelve failure") {
        runTest {
            coEvery { api.iniciarPago(any(), any()) } returns
                Response.success<com.example.sanaescencia.data.remote.dto.InicioPagoResponse>(null)

            val result = repository.iniciarPago("tk", 100)

            result.isFailure shouldBe true
        }
    }

    test("pago con error HTTP devuelve failure") {
        runTest {
            coEvery { api.iniciarPago(any(), any()) } returns responseHttpError(500)

            val result = repository.iniciarPago("tk", 100)

            result.exceptionOrNull()?.message shouldBe "Error HTTP 500"
        }
    }

    test("pago captura excepciones") {
        runTest {
            coEvery { api.iniciarPago(any(), any()) } throws IOException("pago")

            val result = repository.iniciarPago("tk", 100)

            result.exceptionOrNull().shouldBeInstanceOf<IOException>()
        }
    }

    // --- Cancelacion de corrutina ---

    test("propaga CancellationException en cada endpoint") {
        coEvery { api.obtenerReservas(any()) } throws CancellationException("cancelado")
        coEvery { api.obtenerReservaPorId(any(), any()) } throws CancellationException("cancelado")
        coEvery { api.crearReserva(any(), any()) } throws CancellationException("cancelado")
        coEvery { api.cancelarReserva(any(), any()) } throws CancellationException("cancelado")
        coEvery { api.iniciarPago(any(), any()) } throws CancellationException("cancelado")
        coEvery { api.obtenerHorariosDisponibles(any(), any(), any(), any(), any()) } throws
            CancellationException("cancelado")

        runBlocking {
            shouldThrow<CancellationException> { repository.obtenerReservas("t") }
            shouldThrow<CancellationException> { repository.obtenerReservaPorId("t", 1) }
            shouldThrow<CancellationException> {
                repository.crearReserva("t", TestData.reservaRequest)
            }
            shouldThrow<CancellationException> { repository.cancelarReserva("t", 1) }
            shouldThrow<CancellationException> { repository.iniciarPago("t", 1) }
            shouldThrow<CancellationException> {
                repository.obtenerHorariosDisponibles(
                    "t", 1, 2, LocalDate.of(2026, 1, 1), "Online"
                )
            }
        }
    }
})
