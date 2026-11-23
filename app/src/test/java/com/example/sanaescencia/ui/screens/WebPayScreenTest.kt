package com.example.sanaescencia.ui.screens

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.sanaescencia.testutil.setThemedContent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class WebPayScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private var pagoFinalizado = 0
    private var volverReservas = 0

    private fun render(estadoFinal: String? = null) {
        composeRule.setThemedContent {
            WebpayScreen(
                url = "https://webpay.test/pagar",
                token = "token-ws",
                estadoFinal = estadoFinal,
                onPagoFinalizado = { pagoFinalizado++ },
                onVolverReservas = { volverReservas++ }
            )
        }
    }

    @Test
    fun `sin estado final muestra la pantalla de conexion`() {
        render()

        composeRule.onNodeWithText("Conectando con Webpay...").assertExists()
        composeRule.onNodeWithText("Estamos preparando tu pago seguro.").assertExists()
    }

    @Test
    fun `el estado no concretada muestra su resultado`() {
        render("NO_CONCRETADA")

        composeRule.onNodeWithText("Proceso de reserva anulado").assertExists()
        composeRule.onNodeWithText(
            "Tu reserva no se realizó y el horario vuelve a estar disponible. Puedes agendar una nueva atención cuando quieras."
        ).assertExists()
    }

    @Test
    fun `el estado expirada muestra su resultado`() {
        render("EXPIRADA")

        composeRule.onNodeWithText("El tiempo de reserva terminó").assertExists()
        composeRule.onNodeWithText(
            "No alcanzaste a completar tu reserva. Puedes seleccionar un nuevo horario e intentarlo nuevamente."
        ).assertExists()
    }

    @Test
    fun `cualquier otro estado muestra reserva cancelada`() {
        render("CANCELADA")

        composeRule.onNodeWithText("Reserva cancelada").assertExists()
        composeRule.onNodeWithText(
            "Tu reserva fue cancelada. Puedes volver a reservas para agendar una nueva atención."
        ).assertExists()
    }

    @Test
    fun `el boton volver a reservas dispara el callback`() {
        render("EXPIRADA")

        composeRule.onNodeWithText("Volver a reservas").performClick()

        assertEquals(1, volverReservas)
    }

    @Test
    fun `esRetorno reconoce el path del backend`() {
        assertTrue(
            invocarEsRetorno("https://sanaesencia-app.duckdns.org/api/pagos/webpay/retorno")
        )
        assertTrue(invocarEsRetorno("/api/pagos/webpay/retorno"))
    }

    @Test
    fun `esRetorno rechaza otras rutas y valores nulos`() {
        assertFalse(invocarEsRetorno(null))
        assertFalse(invocarEsRetorno("https://sanaesencia-app.duckdns.org/otra"))
        assertFalse(invocarEsRetorno("no-es-url"))
    }

    private fun invocarEsRetorno(url: String?): Boolean {
        val metodo = Class.forName("com.example.sanaescencia.ui.screens.WebPayScreenKt")
            .getDeclaredMethod("esRetorno", String::class.java)
            .apply { isAccessible = true }
        return metodo.invoke(null, url) as Boolean
    }
}
