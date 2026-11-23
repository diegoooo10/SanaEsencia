package com.example.sanaescencia.ui.screens

import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.sanaescencia.testutil.setThemedContent
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Ejercita los callbacks del [android.webkit.WebViewClient] de la pantalla de
 * Webpay sin red. La WebView se localiza recorriendo la jerarquia de vistas
 * creada por `AndroidView`.
 */
@RunWith(RobolectricTestRunner::class)
class WebPayWebViewTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private var pagoFinalizado = 0

    private fun render() {
        composeRule.setThemedContent {
            WebpayScreen(
                url = "https://webpay.test/pagar",
                token = "token-ws",
                estadoFinal = null,
                onPagoFinalizado = { pagoFinalizado++ },
                onVolverReservas = {}
            )
        }
    }

    private fun webView(): WebView {
        fun buscar(vista: View): WebView? {
            if (vista is WebView) return vista
            if (vista is ViewGroup) {
                for (indice in 0 until vista.childCount) {
                    buscar(vista.getChildAt(indice))?.let { return it }
                }
            }
            return null
        }

        return buscar(composeRule.activity.window.decorView)
            ?: throw AssertionError("No se encontro la WebView de Webpay")
    }

    private fun request(url: String, mainFrame: Boolean = true): WebResourceRequest =
        object : WebResourceRequest {
            override fun getUrl(): Uri = Uri.parse(url)
            override fun isForMainFrame(): Boolean = mainFrame
            override fun isRedirect(): Boolean = false
            override fun hasGesture(): Boolean = false
            override fun getMethod(): String = "GET"
            override fun getRequestHeaders(): MutableMap<String, String> = mutableMapOf()
        }

    @Test
    fun `un error http en la pagina principal muestra el aviso`() {
        render()
        val webView = webView()

        webView.webViewClient.onReceivedHttpError(
            webView,
            request("https://webpay.test/pagar"),
            null
        )
        composeRule.waitForIdle()

        composeRule.onNodeWithText("No pudimos cargar la página de pago.").assertExists()
        composeRule.onNodeWithText(
            "El resultado de tu pago aún no está confirmado. Puedes consultar tu reserva."
        ).assertExists()

        composeRule.onNodeWithText("Consultar reserva").performClick()

        assertEquals(1, pagoFinalizado)
    }

    @Test
    fun `un error de un recurso secundario no muestra el aviso`() {
        render()
        val webView = webView()

        webView.webViewClient.onReceivedHttpError(
            webView,
            request("https://webpay.test/recurso.css", mainFrame = false),
            null
        )
        composeRule.waitForIdle()

        composeRule.onNodeWithText("No pudimos cargar la página de pago.").assertDoesNotExist()
    }

    @Test
    fun `un error http en el retorno confirma el pago`() {
        render()
        val webView = webView()

        webView.webViewClient.onReceivedHttpError(
            webView,
            request("https://sanaesencia-app.duckdns.org/api/pagos/webpay/retorno"),
            null
        )
        composeRule.waitForIdle()

        assertEquals(1, pagoFinalizado)
        composeRule.onNodeWithText("Verificando tu pago...").assertExists()
    }

    @Test
    fun `el inicio del retorno muestra la verificacion`() {
        render()
        val webView = webView()

        webView.webViewClient.onPageStarted(
            webView,
            "https://sanaesencia-app.duckdns.org/api/pagos/webpay/retorno",
            null
        )
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Verificando tu pago...").assertExists()
        composeRule.onNodeWithText("Estamos consultando el resultado de tu transacción.")
            .assertExists()
        assertEquals(0, pagoFinalizado)
    }

    @Test
    fun `una pagina segura sin retorno termina la carga`() {
        render()
        val webView = webView()

        webView.webViewClient.onPageFinished(webView, "https://webpay.test/pagar")
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Conectando con Webpay...").assertDoesNotExist()
        composeRule.onNodeWithText("Verificando tu pago...").assertDoesNotExist()
    }
}
