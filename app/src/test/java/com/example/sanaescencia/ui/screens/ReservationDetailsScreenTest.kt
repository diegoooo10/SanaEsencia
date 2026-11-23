package com.example.sanaescencia.ui.screens

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.example.sanaescencia.testutil.TestData
import com.example.sanaescencia.testutil.setThemedContent
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ReservationDetailsScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private var backCalls = 0
    private var cancelCalls = 0

    private fun render(estado: String = "Confirmada") {
        composeRule.setThemedContent {
            ReservationDetailScreen(
                reserva = TestData.reserva.copy(estado = estado),
                onBack = { backCalls++ },
                onCancel = { cancelCalls++ }
            )
        }
    }

    @Test
    fun `muestra toda la informacion de la reserva`() {
        render()

        composeRule.onNodeWithText("Detalle de reserva").assertExists()
        composeRule.onNodeWithText("Confirmada").assertExists()
        composeRule.onNodeWithText("Terapia individual").assertExists()
        composeRule.onNodeWithText("Reserva #0100").assertExists()
        composeRule.onNodeWithText("Información de la sesión").assertExists()
        composeRule.onNodeWithText("Psicología").assertExists()
        composeRule.onNodeWithText("Dra. Ana Soto").assertExists()
        composeRule.onNodeWithText("10:00 hrs").assertExists()
        composeRule.onNodeWithText("60 minutos").assertExists()
        composeRule.onNodeWithText("Presencial").assertExists()
        composeRule.onNodeWithText("Importante").assertExists()
        composeRule.onNodeWithText(
            "Las cancelaciones deben realizarse con al menos 12 horas de anticipación respecto al horario de la sesión."
        ).assertExists()
    }

    @Test
    fun `formatea la fecha en espanol`() {
        render()

        composeRule.onNodeWithText("01 de diciembre de 2026").assertExists()
    }

    @Test
    fun `el boton volver dispara onBack`() {
        render()

        composeRule.onNodeWithContentDescription("Volver").performClick()

        assertEquals(1, backCalls)
    }

    @Test
    fun `una reserva confirmada permite cancelar tras confirmar el dialogo`() {
        render("Confirmada")

        composeRule.onNodeWithText("Cancelar reserva").performScrollTo().performClick()
        composeRule.onNodeWithText("¿Estás seguro de que deseas cancelar esta reserva?")
            .assertExists()

        composeRule.onNodeWithText("Sí, cancelar").performClick()

        assertEquals(1, cancelCalls)
    }

    @Test
    fun `el dialogo de cancelacion puede cerrarse sin cancelar`() {
        render("Confirmada")

        composeRule.onNodeWithText("Cancelar reserva").performScrollTo().performClick()
        composeRule.onNodeWithText("Volver").performClick()

        assertEquals(0, cancelCalls)
        composeRule.onNodeWithText("¿Estás seguro de que deseas cancelar esta reserva?")
            .assertDoesNotExist()
    }

    @Test
    fun `una reserva cancelada no muestra la opcion de cancelar`() {
        render("Cancelada")

        composeRule.onNodeWithText("Cancelada").assertExists()
        composeRule.onNodeWithText("Cancelar reserva").assertDoesNotExist()
    }

    @Test
    fun `una reserva pendiente tampoco muestra la opcion de cancelar`() {
        render("Pendiente")

        composeRule.onNodeWithText("Cancelar reserva").assertDoesNotExist()
    }

    @Test
    fun `el identificador se rellena con ceros a la izquierda`() {
        composeRule.setThemedContent {
            ReservationDetailScreen(
                reserva = TestData.reserva.copy(id = 7),
                onCancel = {}
            )
        }

        composeRule.onNodeWithText("Reserva #0007").assertExists()
    }
}
