package com.example.sanaescencia.ui.screens

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.example.sanaescencia.data.model.Especialidad
import com.example.sanaescencia.data.model.Profesional
import com.example.sanaescencia.data.model.Servicio
import com.example.sanaescencia.testutil.FakeReservaRepository
import com.example.sanaescencia.testutil.TestData
import com.example.sanaescencia.testutil.setThemedContent
import com.example.sanaescencia.ui.viewmodel.ReservaViewModel
import kotlinx.coroutines.CompletableDeferred
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.LocalTime

@RunWith(RobolectricTestRunner::class)
class NewReservationScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val repository = FakeReservaRepository()
    private val viewModel = ReservaViewModel(repository)

    private val profesional = TestData.profesional
    private val especialidad = TestData.especialidad
    private val servicio = TestData.servicio

    private fun render(
        profesionales: List<Profesional> = listOf(profesional),
        especialidades: List<Especialidad> = listOf(especialidad),
        servicios: List<Servicio> = listOf(servicio),
        token: String = "token-prueba"
    ) {
        composeRule.setThemedContent {
            NewReservationScreen(
                profesionales = profesionales,
                especialidades = especialidades,
                servicios = servicios,
                token = token,
                reservaViewModel = viewModel
            )
        }
    }

    private fun click(texto: String) {
        composeRule.onNodeWithText(texto).performScrollTo().performClick()
        composeRule.waitForIdle()
    }

    /**
     * Flujo comun hasta la seleccion de fecha. Las fechas del selector estan
     * fijas en produccion (2026-11-17..2026-11-21); se usan tal cual.
     */
    private fun seleccionarHastaFecha() {
        click("Psicología")
        click("Dra. Ana Soto")
        click("Terapia individual")
        click("Online")
        click("Mar 17")
    }

    private fun seleccionarHastaHora() {
        seleccionarHastaFecha()
        click("09:00")
    }

    @Test
    fun `muestra el titulo y las especialidades disponibles`() {
        render()

        composeRule.onNodeWithText("Nueva reserva").assertExists()
        composeRule.onNodeWithText(
            "Selecciona tu atención y encuentra el horario que mejor te acomode."
        ).assertExists()
        composeRule.onNodeWithText("1. Selecciona una especialidad").assertExists()
        composeRule.onNodeWithText("Psicología").assertExists()
        composeRule.onNodeWithText("Atención psicológica integral").assertExists()
    }

    @Test
    fun `oculta especialidades sin profesionales disponibles`() {
        val sinProfesional = Especialidad(99, "Nutrición", "Sin profesionales")

        render(especialidades = listOf(especialidad, sinProfesional))

        composeRule.onNodeWithText("Psicología").assertExists()
        composeRule.onNodeWithText("Nutrición").assertDoesNotExist()
    }

    @Test
    fun `oculta especialidades cuyos profesionales no estan disponibles`() {
        val ocupado = profesional.copy(estadoAgenda = "OCUPADO")

        render(profesionales = listOf(ocupado))

        composeRule.onNodeWithText("Psicología").assertDoesNotExist()
    }

    @Test
    fun `al elegir especialidad aparecen sus profesionales disponibles`() {
        render()

        click("Psicología")

        composeRule.onNodeWithText("2. Selecciona un profesional").assertExists()
        composeRule.onNodeWithText("Profesionales de Psicología").assertExists()
        composeRule.onNodeWithText("Dra. Ana Soto").assertExists()
    }

    @Test
    fun `al elegir profesional aparecen los servicios de la especialidad`() {
        val otroServicio = Servicio(11, "Terapia de pareja", especialidadId = 2, duracionMinutos = 90)

        render(servicios = listOf(servicio, otroServicio))
        click("Psicología")
        click("Dra. Ana Soto")

        composeRule.onNodeWithText("3. Selecciona un servicio").assertExists()
        composeRule.onNodeWithText("Terapia individual").assertExists()
        composeRule.onNodeWithText("Duración: 60 minutos").assertExists()
        composeRule.onNodeWithText("Terapia de pareja").assertDoesNotExist()
    }

    @Test
    fun `al elegir servicio aparecen las modalidades del profesional`() {
        render()
        click("Psicología")
        click("Dra. Ana Soto")
        click("Terapia individual")

        composeRule.onNodeWithText("4. Selecciona la modalidad").assertExists()
        composeRule.onNodeWithText("Online").assertExists()
        composeRule.onNodeWithText("Atención mediante videollamada").assertExists()
        composeRule.onNodeWithText("Presencial").assertExists()
        composeRule.onNodeWithText("Atención en nuestro centro Sanaesencia").assertExists()
    }

    @Test
    fun `al elegir modalidad aparecen las fechas fijas de produccion`() {
        render()
        click("Psicología")
        click("Dra. Ana Soto")
        click("Terapia individual")
        click("Online")

        composeRule.onNodeWithText("5. Selecciona fecha y horario").assertExists()
        composeRule.onNodeWithText("Fechas disponibles").assertExists()
        composeRule.onNodeWithText("Mar 17").assertExists()
        composeRule.onNodeWithText("Mié 18").assertExists()
        composeRule.onNodeWithText("Jue 19").assertExists()
        composeRule.onNodeWithText("Vie 20").assertExists()
        composeRule.onNodeWithText("Sáb 21").assertExists()
    }

    @Test
    fun `al elegir fecha consulta los horarios con los parametros correctos`() {
        render()
        seleccionarHastaFecha()

        assertEquals(1, repository.horariosCalls)
        assertEquals("token-prueba", repository.lastHorariosToken)
        assertEquals(5L, repository.lastProfesionalId)
        assertEquals(10L, repository.lastServicioId)
        assertEquals("2026-11-17", repository.lastFecha?.toString())
        assertEquals("Online", repository.lastModalidad)
        composeRule.onNodeWithText("09:00").assertExists()
        composeRule.onNodeWithText("10:30").assertExists()
    }

    @Test
    fun `sin horarios disponibles muestra un mensaje`() {
        repository.horariosResult = Result.success(emptyList())
        render()
        seleccionarHastaFecha()

        composeRule.onNodeWithText("No hay horarios disponibles para esta fecha.").assertExists()
    }

    @Test
    fun `un error al consultar horarios muestra su mensaje`() {
        repository.horariosResult = Result.failure(IllegalStateException("Sin cupo para esa fecha"))
        render()
        seleccionarHastaFecha()

        composeRule.onNodeWithText("Sin cupo para esa fecha").assertExists()
    }

    @Test
    fun `un error sin mensaje usa el texto por defecto`() {
        repository.horariosResult = Result.failure(IllegalStateException())
        render()
        seleccionarHastaFecha()

        composeRule.onNodeWithText("No se pudieron consultar los horarios.").assertExists()
    }

    @Test
    fun `mientras consulta horarios muestra el indicador de carga`() {
        repository.gate = CompletableDeferred()
        render()
        seleccionarHastaFecha()

        assertTrue(viewModel.isLoadingHorarios.value)
        composeRule.onNodeWithText("Consultando horarios...").assertExists()
    }

    @Test
    fun `sin token no consulta horarios`() {
        render(token = "")
        seleccionarHastaFecha()

        assertEquals(0, repository.horariosCalls)
        assertNull(viewModel.horariosState.value)
    }

    @Test
    fun `al elegir hora muestra el resumen completo`() {
        render()
        seleccionarHastaHora()

        composeRule.onNodeWithText("Resumen de reserva").assertExists()
        composeRule.onNodeWithText("Especialidad").assertExists()
        composeRule.onNodeWithText("Profesional").assertExists()
        composeRule.onNodeWithText("Servicio").assertExists()
        composeRule.onNodeWithText("Modalidad").assertExists()
        composeRule.onNodeWithText("Duración").assertExists()
        composeRule.onNodeWithText("Fecha").assertExists()
        composeRule.onNodeWithText("Hora").assertExists()
        composeRule.onNodeWithText("60 minutos").assertExists()
        composeRule.onNodeWithText("Continuar y pagar").assertExists()
    }

    @Test
    fun `al pagar crea la reserva e inicia el pago`() {
        render()
        seleccionarHastaHora()

        click("Continuar y pagar")

        assertEquals(1, repository.crearCalls)
        val request = repository.lastCrearRequest
        assertEquals(5L, request?.profesionalId)
        assertEquals(10L, request?.servicioId)
        assertEquals("2026-11-17", request?.fecha)
        assertEquals("09:00", request?.hora)
        assertEquals("Online", request?.modalidad)
        assertEquals(1, repository.pagoCalls)
        assertEquals(100L, repository.lastPagoReservaId)
    }

    @Test
    fun `si falla la creacion se reactiva el boton de pago`() {
        repository.crearResult = Result.failure(IllegalStateException("horario tomado"))
        render()
        seleccionarHastaHora()

        click("Continuar y pagar")

        composeRule.onNodeWithText("Continuar y pagar").assertExists()
        assertEquals(0, repository.pagoCalls)
        assertFalse(viewModel.isLoadingCrear.value)
    }

    @Test
    fun `si falla el inicio de pago se reactiva el boton`() {
        repository.pagoResult = Result.failure(IllegalStateException("webpay caido"))
        render()
        seleccionarHastaHora()

        click("Continuar y pagar")

        assertEquals(1, repository.crearCalls)
        composeRule.onNodeWithText("Continuar y pagar").assertExists()
    }

    @Test
    fun `el resumen se oculta hasta elegir hora`() {
        render()
        seleccionarHastaFecha()

        composeRule.onNodeWithText("Resumen de reserva").assertDoesNotExist()
        composeRule.onNodeWithText("Continuar y pagar").assertDoesNotExist()
    }

    @Test
    fun `cambiar de especialidad reinicia las selecciones posteriores`() {
        val otra = Especialidad(2, "Terapia de pareja", "Enfoque sistémico")
        val profesionalDos = profesional.copy(
            id = 6,
            nombre = "Dr. Otro",
            especialidadesIds = listOf(2)
        )

        render(
            especialidades = listOf(especialidad, otra),
            profesionales = listOf(profesional, profesionalDos)
        )
        click("Psicología")
        click("Dra. Ana Soto")
        click("Terapia de pareja")

        composeRule.onNodeWithText("3. Selecciona un servicio").assertDoesNotExist()
        composeRule.onNodeWithText("Dra. Ana Soto").assertExists()
    }

    @Test
    fun `los horarios se muestran en filas de tres`() {
        repository.horariosResult = Result.success(
            listOf(LocalTime.of(8, 0), LocalTime.of(9, 0), LocalTime.of(10, 0), LocalTime.of(11, 0))
        )
        render()
        seleccionarHastaFecha()

        composeRule.onNodeWithText("08:00").assertExists()
        composeRule.onNodeWithText("09:00").assertExists()
        composeRule.onNodeWithText("10:00").assertExists()
        composeRule.onNodeWithText("11:00").assertExists()
    }
}
