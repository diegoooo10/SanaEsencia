package com.example.sanaescencia.ui.viewmodel

import com.example.sanaescencia.data.model.Reserva
import com.example.sanaescencia.data.remote.dto.InicioPagoResponse
import com.example.sanaescencia.data.repository.ReservaRepository
import com.example.sanaescencia.testutil.MainDispatcherController
import com.example.sanaescencia.testutil.TestData
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import java.io.IOException
import java.time.LocalDate
import java.time.LocalTime

class ReservaViewModelTest : FunSpec({

    val mainDispatcher = MainDispatcherController()

    lateinit var repository: ReservaRepository
    lateinit var viewModel: ReservaViewModel

    val fecha = LocalDate.of(2026, 12, 1)

    beforeTest {
        mainDispatcher.install()
        repository = mockk()
        viewModel = ReservaViewModel(repository)
    }

    afterTest {
        mainDispatcher.reset()
    }

    test("estados iniciales vacios y sin carga") {
        viewModel.horariosState.value shouldBe null
        viewModel.reservasState.value shouldBe null
        viewModel.reservaDetalleState.value shouldBe null
        viewModel.crearReservaState.value shouldBe null
        viewModel.cancelacionState.value shouldBe null
        viewModel.inicioPagoState.value shouldBe null
        viewModel.isLoadingHorarios.value shouldBe false
        viewModel.isLoadingReservas.value shouldBe false
        viewModel.isLoadingDetalle.value shouldBe false
        viewModel.isLoadingCrear.value shouldBe false
        viewModel.isLoadingCancelacion.value shouldBe false
        viewModel.isLoadingPago.value shouldBe false
    }

    test("obtiene horarios y envia los parametros correctos") {
        coEvery {
            repository.obtenerHorariosDisponibles("tk", 3L, 4L, fecha, "Online")
        } returns Result.success(listOf(LocalTime.of(8, 0)))

        viewModel.obtenerHorariosDisponibles("tk", 3, 4, fecha, "Online")

        viewModel.horariosState.value?.getOrNull() shouldBe listOf(LocalTime.of(8, 0))
        coVerify(exactly = 1) {
            repository.obtenerHorariosDisponibles("tk", 3L, 4L, fecha, "Online")
        }
        viewModel.isLoadingHorarios.value shouldBe false
    }

    test("horarios con failure se publican") {
        coEvery { repository.obtenerHorariosDisponibles(any(), any(), any(), any(), any()) } returns
            Result.failure(IllegalStateException("sin cupo"))

        viewModel.obtenerHorariosDisponibles("tk", 1, 2, fecha, "Presencial")

        viewModel.horariosState.value?.exceptionOrNull()?.message shouldBe "sin cupo"
    }

    test("horarios capturan excepciones") {
        coEvery { repository.obtenerHorariosDisponibles(any(), any(), any(), any(), any()) } throws
            IOException("red")

        viewModel.obtenerHorariosDisponibles("tk", 1, 2, fecha, "Presencial")

        viewModel.horariosState.value?.exceptionOrNull().shouldBeInstanceOf<IOException>()
        viewModel.isLoadingHorarios.value shouldBe false
    }

    test("marca loading de horarios mientras espera") {
        val gate = CompletableDeferred<Unit>()
        coEvery { repository.obtenerHorariosDisponibles(any(), any(), any(), any(), any()) } coAnswers {
            gate.await()
            Result.success(listOf(LocalTime.of(8, 0)))
        }

        viewModel.obtenerHorariosDisponibles("tk", 1, 2, fecha, "Presencial")

        viewModel.isLoadingHorarios.value shouldBe true

        gate.complete(Unit)

        viewModel.isLoadingHorarios.value shouldBe false
    }

    test("ignora horarios concurrentes") {
        val gate = CompletableDeferred<Unit>()
        coEvery { repository.obtenerHorariosDisponibles(any(), any(), any(), any(), any()) } coAnswers {
            gate.await()
            Result.success(listOf(LocalTime.of(8, 0)))
        }

        viewModel.obtenerHorariosDisponibles("tk", 1, 2, fecha, "Presencial")
        viewModel.obtenerHorariosDisponibles("tk", 1, 2, fecha, "Presencial")

        coVerify(exactly = 1) {
            repository.obtenerHorariosDisponibles(any(), any(), any(), any(), any())
        }
    }

    test("obtiene reservas") {
        coEvery { repository.obtenerReservas("tk") } returns
            Result.success(listOf(TestData.reserva))

        viewModel.obtenerReservas("tk")

        viewModel.reservasState.value?.getOrNull() shouldBe listOf(TestData.reserva)
        coVerify(exactly = 1) { repository.obtenerReservas("tk") }
        viewModel.isLoadingReservas.value shouldBe false
    }

    test("reservas con failure y excepcion") {
        coEvery { repository.obtenerReservas(any()) } returns
            Result.failure(IllegalStateException("no")) andThenThrows IOException("red")

        viewModel.obtenerReservas("tk")
        viewModel.reservasState.value?.exceptionOrNull()?.message shouldBe "no"

        viewModel.obtenerReservas("tk")
        viewModel.reservasState.value?.exceptionOrNull().shouldBeInstanceOf<IOException>()
    }

    test("ignora reservas concurrentes") {
        val gate = CompletableDeferred<Unit>()
        coEvery { repository.obtenerReservas(any()) } coAnswers {
            gate.await()
            Result.success(listOf(TestData.reserva))
        }

        viewModel.obtenerReservas("tk")
        viewModel.obtenerReservas("tk")

        coVerify(exactly = 1) { repository.obtenerReservas(any()) }
    }

    test("obtiene detalle de reserva") {
        coEvery { repository.obtenerReservaPorId("tk", 100L) } returns
            Result.success(TestData.reserva)

        viewModel.obtenerReservaPorId("tk", 100)

        viewModel.reservaDetalleState.value?.getOrNull() shouldBe TestData.reserva
        coVerify(exactly = 1) { repository.obtenerReservaPorId("tk", 100L) }
        viewModel.isLoadingDetalle.value shouldBe false
    }

    test("detalle con failure y excepcion") {
        coEvery { repository.obtenerReservaPorId(any(), any()) } returns
            Result.failure(IllegalStateException("404")) andThenThrows IOException("red")

        viewModel.obtenerReservaPorId("tk", 1)
        viewModel.reservaDetalleState.value?.exceptionOrNull()?.message shouldBe "404"

        viewModel.obtenerReservaPorId("tk", 1)
        viewModel.reservaDetalleState.value?.exceptionOrNull()
            .shouldBeInstanceOf<IOException>()
    }

    test("crea reserva y envia el request") {
        coEvery { repository.crearReserva("tk", TestData.reservaRequest) } returns
            Result.success(TestData.reserva)

        viewModel.crearReserva("tk", TestData.reservaRequest)

        viewModel.crearReservaState.value?.getOrNull() shouldBe TestData.reserva
        coVerify(exactly = 1) { repository.crearReserva("tk", TestData.reservaRequest) }
        viewModel.isLoadingCrear.value shouldBe false
    }

    test("creacion con failure y excepcion") {
        coEvery { repository.crearReserva(any(), any()) } returns
            Result.failure(IllegalStateException("horario ocupado")) andThenThrows
            IOException("red")

        viewModel.crearReserva("tk", TestData.reservaRequest)
        viewModel.crearReservaState.value?.exceptionOrNull()?.message shouldBe
            "horario ocupado"

        viewModel.crearReserva("tk", TestData.reservaRequest)
        viewModel.crearReservaState.value?.exceptionOrNull().shouldBeInstanceOf<IOException>()
    }

    test("ignora creaciones concurrentes") {
        val gate = CompletableDeferred<Unit>()
        coEvery { repository.crearReserva(any(), any()) } coAnswers {
            gate.await()
            Result.success(TestData.reserva)
        }

        viewModel.crearReserva("tk", TestData.reservaRequest)
        viewModel.crearReserva("tk", TestData.reservaRequest)

        coVerify(exactly = 1) { repository.crearReserva(any(), any()) }
    }

    test("cancela reserva") {
        coEvery { repository.cancelarReserva("tk", 9L) } returns Result.success(Unit)

        viewModel.cancelarReserva("tk", 9)

        viewModel.cancelacionState.value?.getOrNull() shouldBe Unit
        coVerify(exactly = 1) { repository.cancelarReserva("tk", 9L) }
        viewModel.isLoadingCancelacion.value shouldBe false
    }

    test("cancelacion con failure y excepcion") {
        coEvery { repository.cancelarReserva(any(), any()) } returns
            Result.failure(IllegalStateException("muy tarde")) andThenThrows
            IOException("red")

        viewModel.cancelarReserva("tk", 9)
        viewModel.cancelacionState.value?.exceptionOrNull()?.message shouldBe "muy tarde"

        viewModel.cancelarReserva("tk", 9)
        viewModel.cancelacionState.value?.exceptionOrNull().shouldBeInstanceOf<IOException>()
    }

    test("ignora cancelaciones concurrentes") {
        val gate = CompletableDeferred<Unit>()
        coEvery { repository.cancelarReserva(any(), any()) } coAnswers {
            gate.await()
            Result.success(Unit)
        }

        viewModel.cancelarReserva("tk", 9)
        viewModel.cancelarReserva("tk", 9)

        coVerify(exactly = 1) { repository.cancelarReserva(any(), any()) }
    }

    test("inicia pago") {
        coEvery { repository.iniciarPago("tk", 100L) } returns
            Result.success(TestData.inicioPago)

        viewModel.iniciarPago("tk", 100)

        viewModel.inicioPagoState.value?.getOrNull() shouldBe TestData.inicioPago
        coVerify(exactly = 1) { repository.iniciarPago("tk", 100L) }
        viewModel.isLoadingPago.value shouldBe false
    }

    test("pago con failure y excepcion") {
        coEvery { repository.iniciarPago(any(), any()) } returns
            Result.failure(IllegalStateException("rechazado")) andThenThrows
            IOException("red")

        viewModel.iniciarPago("tk", 100)
        viewModel.inicioPagoState.value?.exceptionOrNull()?.message shouldBe "rechazado"

        viewModel.iniciarPago("tk", 100)
        viewModel.inicioPagoState.value?.exceptionOrNull().shouldBeInstanceOf<IOException>()
    }

    test("ignora pagos concurrentes") {
        val gate = CompletableDeferred<Unit>()
        coEvery { repository.iniciarPago(any(), any()) } coAnswers {
            gate.await()
            Result.success(TestData.inicioPago)
        }

        viewModel.iniciarPago("tk", 100)
        viewModel.iniciarPago("tk", 100)

        coVerify(exactly = 1) { repository.iniciarPago(any(), any()) }
    }

    test("una CancellationException no deja loading activo") {
        coEvery { repository.obtenerReservas(any()) } throws
            CancellationException("cancelado")

        viewModel.obtenerReservas("tk")

        viewModel.isLoadingReservas.value shouldBe false
        viewModel.reservasState.value shouldBe null
    }

    test("limpiar todos los estados") {
        coEvery { repository.obtenerHorariosDisponibles(any(), any(), any(), any(), any()) } returns
            Result.success(listOf(LocalTime.of(8, 0)))
        coEvery { repository.obtenerReservas(any()) } returns
            Result.success(listOf(TestData.reserva))
        coEvery { repository.obtenerReservaPorId(any(), any()) } returns
            Result.success(TestData.reserva)
        coEvery { repository.crearReserva(any(), any()) } returns
            Result.success(TestData.reserva)
        coEvery { repository.cancelarReserva(any(), any()) } returns Result.success(Unit)
        coEvery { repository.iniciarPago(any(), any()) } returns
            Result.success(TestData.inicioPago)

        viewModel.obtenerHorariosDisponibles("tk", 1, 2, fecha, "Online")
        viewModel.obtenerReservas("tk")
        viewModel.obtenerReservaPorId("tk", 1)
        viewModel.crearReserva("tk", TestData.reservaRequest)
        viewModel.cancelarReserva("tk", 1)
        viewModel.iniciarPago("tk", 1)

        viewModel.limpiarHorarios()
        viewModel.limpiarReservas()
        viewModel.limpiarDetalle()
        viewModel.limpiarCreacion()
        viewModel.limpiarCancelacion()
        viewModel.limpiarInicioPago()

        viewModel.horariosState.value shouldBe null
        viewModel.reservasState.value shouldBe null
        viewModel.reservaDetalleState.value shouldBe null
        viewModel.crearReservaState.value shouldBe null
        viewModel.cancelacionState.value shouldBe null
        viewModel.inicioPagoState.value shouldBe null
    }
})
