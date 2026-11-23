package com.example.sanaescencia.ui.viewmodel

import com.example.sanaescencia.testutil.FakeReservaRepository
import com.example.sanaescencia.testutil.MainDispatcherRule
import com.example.sanaescencia.testutil.TestData
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.IOException
import java.time.LocalDate
import java.time.LocalTime

class ReservaViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeReservaRepository()
    private val viewModel = ReservaViewModel(repository)

    private val fecha = LocalDate.of(2026, 12, 1)

    @Test
    fun `estados iniciales vacios y sin carga`() {
        assertNull(viewModel.horariosState.value)
        assertNull(viewModel.reservasState.value)
        assertNull(viewModel.reservaDetalleState.value)
        assertNull(viewModel.crearReservaState.value)
        assertNull(viewModel.cancelacionState.value)
        assertNull(viewModel.inicioPagoState.value)
        assertFalse(viewModel.isLoadingHorarios.value)
        assertFalse(viewModel.isLoadingReservas.value)
        assertFalse(viewModel.isLoadingDetalle.value)
        assertFalse(viewModel.isLoadingCrear.value)
        assertFalse(viewModel.isLoadingCancelacion.value)
        assertFalse(viewModel.isLoadingPago.value)
    }

    @Test
    fun `obtiene horarios y envia los parametros correctos`() {
        repository.horariosResult = Result.success(listOf(LocalTime.of(8, 0)))

        viewModel.obtenerHorariosDisponibles("tk", 3, 4, fecha, "Online")

        assertEquals(
            listOf(LocalTime.of(8, 0)),
            viewModel.horariosState.value?.getOrNull()
        )
        assertEquals("tk", repository.lastHorariosToken)
        assertEquals(3L, repository.lastProfesionalId)
        assertEquals(4L, repository.lastServicioId)
        assertEquals(fecha, repository.lastFecha)
        assertEquals("Online", repository.lastModalidad)
        assertFalse(viewModel.isLoadingHorarios.value)
    }

    @Test
    fun `horarios con failure se publican`() {
        repository.horariosResult = Result.failure(IllegalStateException("sin cupo"))

        viewModel.obtenerHorariosDisponibles("tk", 1, 2, fecha, "Presencial")

        assertEquals("sin cupo", viewModel.horariosState.value?.exceptionOrNull()?.message)
    }

    @Test
    fun `horarios capturan excepciones`() {
        repository.throwable = IOException("red")

        viewModel.obtenerHorariosDisponibles("tk", 1, 2, fecha, "Presencial")

        assertTrue(viewModel.horariosState.value?.exceptionOrNull() is IOException)
        assertFalse(viewModel.isLoadingHorarios.value)
    }

    @Test
    fun `marca loading de horarios mientras espera`() {
        val gate = CompletableDeferred<Unit>()
        repository.gate = gate

        viewModel.obtenerHorariosDisponibles("tk", 1, 2, fecha, "Presencial")

        assertTrue(viewModel.isLoadingHorarios.value)

        gate.complete(Unit)

        assertFalse(viewModel.isLoadingHorarios.value)
    }

    @Test
    fun `ignora horarios concurrentes`() {
        repository.gate = CompletableDeferred()

        viewModel.obtenerHorariosDisponibles("tk", 1, 2, fecha, "Presencial")
        viewModel.obtenerHorariosDisponibles("tk", 1, 2, fecha, "Presencial")

        assertEquals(1, repository.horariosCalls)
    }

    @Test
    fun `obtiene reservas`() {
        repository.reservasResult = Result.success(listOf(TestData.reserva))

        viewModel.obtenerReservas("tk")

        assertEquals(listOf(TestData.reserva), viewModel.reservasState.value?.getOrNull())
        assertEquals("tk", repository.lastReservasToken)
        assertFalse(viewModel.isLoadingReservas.value)
    }

    @Test
    fun `reservas con failure y excepcion`() {
        repository.reservasResult = Result.failure(IllegalStateException("no"))
        viewModel.obtenerReservas("tk")
        assertEquals("no", viewModel.reservasState.value?.exceptionOrNull()?.message)

        repository.throwable = IOException("red")
        viewModel.obtenerReservas("tk")
        assertTrue(viewModel.reservasState.value?.exceptionOrNull() is IOException)
    }

    @Test
    fun `ignora reservas concurrentes`() {
        repository.gate = CompletableDeferred()

        viewModel.obtenerReservas("tk")
        viewModel.obtenerReservas("tk")

        assertEquals(1, repository.reservasCalls)
    }

    @Test
    fun `obtiene detalle de reserva`() {
        repository.detalleResult = Result.success(TestData.reserva)

        viewModel.obtenerReservaPorId("tk", 100)

        assertEquals(TestData.reserva, viewModel.reservaDetalleState.value?.getOrNull())
        assertEquals(100L, repository.lastDetalleId)
        assertFalse(viewModel.isLoadingDetalle.value)
    }

    @Test
    fun `detalle con failure y excepcion`() {
        repository.detalleResult = Result.failure(IllegalStateException("404"))
        viewModel.obtenerReservaPorId("tk", 1)
        assertEquals("404", viewModel.reservaDetalleState.value?.exceptionOrNull()?.message)

        repository.throwable = IOException("red")
        viewModel.obtenerReservaPorId("tk", 1)
        assertTrue(viewModel.reservaDetalleState.value?.exceptionOrNull() is IOException)
    }

    @Test
    fun `crea reserva y envia el request`() {
        repository.crearResult = Result.success(TestData.reserva)

        viewModel.crearReserva("tk", TestData.reservaRequest)

        assertEquals(TestData.reserva, viewModel.crearReservaState.value?.getOrNull())
        assertEquals(TestData.reservaRequest, repository.lastCrearRequest)
        assertFalse(viewModel.isLoadingCrear.value)
    }

    @Test
    fun `creacion con failure y excepcion`() {
        repository.crearResult = Result.failure(IllegalStateException("horario ocupado"))
        viewModel.crearReserva("tk", TestData.reservaRequest)
        assertEquals(
            "horario ocupado",
            viewModel.crearReservaState.value?.exceptionOrNull()?.message
        )

        repository.throwable = IOException("red")
        viewModel.crearReserva("tk", TestData.reservaRequest)
        assertTrue(viewModel.crearReservaState.value?.exceptionOrNull() is IOException)
    }

    @Test
    fun `ignora creaciones concurrentes`() {
        repository.gate = CompletableDeferred()

        viewModel.crearReserva("tk", TestData.reservaRequest)
        viewModel.crearReserva("tk", TestData.reservaRequest)

        assertEquals(1, repository.crearCalls)
    }

    @Test
    fun `cancela reserva`() {
        viewModel.cancelarReserva("tk", 9)

        assertEquals(Unit, viewModel.cancelacionState.value?.getOrNull())
        assertEquals(9L, repository.lastCancelarId)
        assertFalse(viewModel.isLoadingCancelacion.value)
    }

    @Test
    fun `cancelacion con failure y excepcion`() {
        repository.cancelarResult = Result.failure(IllegalStateException("muy tarde"))
        viewModel.cancelarReserva("tk", 9)
        assertEquals("muy tarde", viewModel.cancelacionState.value?.exceptionOrNull()?.message)

        repository.throwable = IOException("red")
        viewModel.cancelarReserva("tk", 9)
        assertTrue(viewModel.cancelacionState.value?.exceptionOrNull() is IOException)
    }

    @Test
    fun `ignora cancelaciones concurrentes`() {
        repository.gate = CompletableDeferred()

        viewModel.cancelarReserva("tk", 9)
        viewModel.cancelarReserva("tk", 9)

        assertEquals(1, repository.cancelarCalls)
    }

    @Test
    fun `inicia pago`() {
        viewModel.iniciarPago("tk", 100)

        assertEquals(TestData.inicioPago, viewModel.inicioPagoState.value?.getOrNull())
        assertEquals(100L, repository.lastPagoReservaId)
        assertFalse(viewModel.isLoadingPago.value)
    }

    @Test
    fun `pago con failure y excepcion`() {
        repository.pagoResult = Result.failure(IllegalStateException("rechazado"))
        viewModel.iniciarPago("tk", 100)
        assertEquals("rechazado", viewModel.inicioPagoState.value?.exceptionOrNull()?.message)

        repository.throwable = IOException("red")
        viewModel.iniciarPago("tk", 100)
        assertTrue(viewModel.inicioPagoState.value?.exceptionOrNull() is IOException)
    }

    @Test
    fun `ignora pagos concurrentes`() {
        repository.gate = CompletableDeferred()

        viewModel.iniciarPago("tk", 100)
        viewModel.iniciarPago("tk", 100)

        assertEquals(1, repository.pagoCalls)
    }

    @Test
    fun `una CancellationException no deja loading activo`() {
        repository.throwable = CancellationException("cancelado")

        viewModel.obtenerReservas("tk")

        assertFalse(viewModel.isLoadingReservas.value)
        assertNull(viewModel.reservasState.value)
    }

    @Test
    fun `limpiar todos los estados`() {
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

        assertNull(viewModel.horariosState.value)
        assertNull(viewModel.reservasState.value)
        assertNull(viewModel.reservaDetalleState.value)
        assertNull(viewModel.crearReservaState.value)
        assertNull(viewModel.cancelacionState.value)
        assertNull(viewModel.inicioPagoState.value)
    }
}
