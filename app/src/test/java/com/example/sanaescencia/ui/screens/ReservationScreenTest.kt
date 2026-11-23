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

@RunWith(RobolectricTestRunner::class)
class ReservationScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private var nuevaReservaCalls = 0
    private var detalleId: Long? = null

    private fun reserva(
        id: Long = 1,
        servicio: String = "Terapia individual",
        estado: String = "Confirmada"
    ) = TestData.reserva.copy(
        id = id,
        servicio = servicio,
        estado = estado,
        fecha = LocalDate.now().plusDays(3)
    )

    private fun render(reservas: List<Reserva>) {
        composeRule.setThemedContent {
            ReservationScreen(
                reservas = reservas,
                onNewReservation = { nuevaReservaCalls++ },
                onReservationDetails = { detalleId = it }
            )
        }
    }

    @Test
    fun `sin reservas muestra el estado vacio de proximas`() {
        render(emptyList())

        composeRule.onNodeWithText("Mis reservas").assertExists()
        composeRule.onNodeWithText("Consulta y administra tus próximas sesiones.").assertExists()
        composeRule.onNodeWithText("Próximas").assertExists()
        composeRule.onNodeWithText("Historial").assertExists()
        composeRule.onNodeWithText("No tienes próximas reservas.").assertExists()
    }

    @Test
    fun `la pestana historial muestra su estado vacio`() {
        render(emptyList())

        composeRule.onNodeWithText("Historial").performClick()

        composeRule.onNodeWithText("Todavía no tienes sesiones realizadas.").assertExists()
    }

    @Test
    fun `muestra las reservas confirmadas y pendientes en proximas`() {
        render(
            listOf(
                reserva(id = 1, servicio = "Confirmada 1", estado = "Confirmada"),
                reserva(id = 2, servicio = "Pendiente 1", estado = "Pendiente")
            )
        )

        composeRule.onNodeWithText("Confirmada 1").assertExists()
        composeRule.onNodeWithText("Pendiente 1").assertExists()
        composeRule.onNodeWithText("Confirmada").assertExists()
        composeRule.onNodeWithText("Pendiente").assertExists()
    }

    @Test
    fun `al pulsar una reserva se solicita su detalle`() {
        render(listOf(reserva(id = 42, servicio = "Masaje")))

        composeRule.onNodeWithText("Masaje").performScrollTo().performClick()

        assertEquals(42L, detalleId)
    }

    @Test
    fun `las reservas realizadas aparecen en el historial`() {
        render(
            listOf(
                reserva(id = 1, servicio = "Futura", estado = "Confirmada"),
                reserva(id = 2, servicio = "Pasada", estado = "Realizada")
            )
        )

        composeRule.onNodeWithText("Historial").performClick()

        composeRule.onNodeWithText("Pasada").assertExists()
        composeRule.onNodeWithText("Futura").assertDoesNotExist()
        composeRule.onNodeWithText("Realizada").assertExists()
    }

    @Test
    fun `las reservas canceladas aparecen en el historial`() {
        render(listOf(reserva(servicio = "Cancelada 1", estado = "Cancelada")))

        composeRule.onNodeWithText("Historial").performClick()

        composeRule.onNodeWithText("Cancelada 1").assertExists()
    }

    @Test
    fun `un estado desconocido no aparece en ninguna pestana`() {
        render(listOf(reserva(servicio = "Rara", estado = "Desconocido")))

        composeRule.onNodeWithText("Rara").assertDoesNotExist()
        composeRule.onNodeWithText("Historial").performClick()
        composeRule.onNodeWithText("Rara").assertDoesNotExist()
    }

    @Test
    fun `el boton nueva reserva dispara el callback`() {
        render(emptyList())

        composeRule.onNodeWithText("Nueva reserva").performClick()

        assertEquals(1, nuevaReservaCalls)
    }
}
