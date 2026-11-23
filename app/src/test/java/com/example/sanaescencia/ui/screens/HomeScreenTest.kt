package com.example.sanaescencia.ui.screens

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.example.sanaescencia.data.model.Reserva
import com.example.sanaescencia.testutil.TestData
import com.example.sanaescencia.testutil.setThemedContent
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.LocalDate
import java.time.LocalTime

@RunWith(RobolectricTestRunner::class)
class HomeScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private var verReservasCalls = 0
    private var nuevaReservaCalls = 0

    private fun reserva(
        id: Long = 1,
        servicio: String = "Terapia individual",
        estado: String = "Confirmada",
        dias: Long = 2,
        hora: LocalTime = LocalTime.of(10, 0),
        modalidad: String = "Presencial"
    ) = TestData.reserva.copy(
        id = id,
        servicio = servicio,
        estado = estado,
        fecha = LocalDate.now().plusDays(dias),
        hora = hora,
        modalidad = modalidad
    )

    private fun render(reservas: List<Reserva> = emptyList()) {
        composeRule.setThemedContent {
            HomeScreen(
                nombreUsuario = "Ana",
                reservas = reservas,
                onViewReservations = { verReservasCalls++ },
                onNewReservation = { nuevaReservaCalls++ }
            )
        }
    }

    @Test
    fun `sin reservas muestra la tarjeta de nueva reserva y accesos rapidos`() {
        render()

        composeRule.onNodeWithText("¡Hola, Ana!").assertExists()
        composeRule.onNodeWithText("Qué bueno tenerte de vuelta.").assertExists()
        composeRule.onNodeWithText("¿TIENES GANAS DE UNA NUEVA SESIÓN?").assertExists()
        composeRule.onNodeWithText("Realiza una nueva reserva").assertExists()
        composeRule.onNodeWithText("¿Qué necesitas hoy?").assertExists()
        composeRule.onNodeWithText("Mis sesiones").assertExists()
        composeRule.onNodeWithText("Mis beneficios").assertExists()
        composeRule.onNodeWithText("PRÓXIMA RESERVA").assertDoesNotExist()
    }

    @Test
    fun `muestra la proxima reserva confirmada`() {
        render(listOf(reserva()))

        composeRule.onNodeWithText("PRÓXIMA RESERVA").assertExists()
        composeRule.onNodeWithText("Terapia individual").assertExists()
        composeRule.onNodeWithText("10:00 horas").assertExists()
        composeRule.onNodeWithText("Presencial").assertExists()
        composeRule.onNodeWithText("Ver mi reserva").assertExists()
        composeRule.onNodeWithText("¿TIENES GANAS DE UNA NUEVA SESIÓN?").assertDoesNotExist()
    }

    @Test
    fun `acepta reservas pendientes sin importar mayusculas`() {
        render(listOf(reserva(estado = "pendiente")))

        composeRule.onNodeWithText("PRÓXIMA RESERVA").assertExists()
    }

    @Test
    fun `ignora reservas canceladas`() {
        render(listOf(reserva(estado = "Cancelada")))

        composeRule.onNodeWithText("PRÓXIMA RESERVA").assertDoesNotExist()
        composeRule.onNodeWithText("¿TIENES GANAS DE UNA NUEVA SESIÓN?").assertExists()
    }

    @Test
    fun `ignora reservas en el pasado`() {
        render(listOf(reserva(dias = -1)))

        composeRule.onNodeWithText("PRÓXIMA RESERVA").assertDoesNotExist()
    }

    @Test
    fun `muestra solo la reserva mas proxima ordenada por fecha y hora`() {
        val temprana = reserva(id = 1, servicio = "Primera", dias = 1, hora = LocalTime.of(9, 0))
        val tardia = reserva(id = 2, servicio = "Segunda", dias = 5, hora = LocalTime.of(8, 0))
        val mismoDiaTarde = reserva(id = 3, servicio = "Tercera", dias = 1, hora = LocalTime.of(18, 0))

        render(listOf(tardia, mismoDiaTarde, temprana))

        composeRule.onNodeWithText("Primera").assertExists()
        composeRule.onNodeWithText("Segunda").assertDoesNotExist()
        composeRule.onNodeWithText("Tercera").assertDoesNotExist()
        composeRule.onNodeWithText("09:00 horas").assertExists()
    }

    @Test
    fun `el boton ver mi reserva navega al listado`() {
        render(listOf(reserva()))

        composeRule.onNodeWithText("Ver mi reserva").performScrollTo().performClick()

        assertEquals(1, verReservasCalls)
    }

    @Test
    fun `la tarjeta de nueva reserva navega al formulario`() {
        render()

        composeRule.onNodeWithText("Realiza una nueva reserva").performScrollTo().performClick()

        assertEquals(1, nuevaReservaCalls)
    }

    @Test
    fun `el acceso rapido mis sesiones navega al listado`() {
        render()

        composeRule.onNodeWithText("Mis sesiones").performScrollTo().performClick()

        assertEquals(1, verReservasCalls)
    }

    @Test
    fun `la modalidad online se muestra en la tarjeta`() {
        render(listOf(reserva(modalidad = "Online")))

        composeRule.onNodeWithText("Online").assertExists()
    }
}
