package com.example.sanaescencia.navigation

import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import com.example.sanaescencia.testutil.FakeProfesionalApi
import com.example.sanaescencia.testutil.FakeReservaApi
import com.example.sanaescencia.testutil.FakeServicioApi
import com.example.sanaescencia.testutil.FakeUsuarioApi
import com.example.sanaescencia.testutil.RetrofitClientInjection
import com.example.sanaescencia.testutil.TestData
import com.example.sanaescencia.testutil.setThemedContent
import com.example.sanaescencia.testutil.textField
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import retrofit2.Response
import java.time.LocalDate


@RunWith(RobolectricTestRunner::class)
class AppNavigationDeepTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private val usuarioApi = FakeUsuarioApi()
    private val profesionalApi = FakeProfesionalApi()
    private val servicioApi = FakeServicioApi()
    private val reservaApi = FakeReservaApi()

    private val reservaFutura = TestData.reserva.copy(
        fecha = LocalDate.now().plusDays(10)
    )

    @Before
    fun setUp() {
        reservaApi.reservasResponse = Response.success(listOf(reservaFutura))
        reservaApi.detalleResponse = Response.success(reservaFutura)
        reservaApi.cancelarResponse = Response.success(
            reservaFutura.copy(estado = "Cancelada")
        )

        RetrofitClientInjection.instalar(
            usuario = usuarioApi,
            profesional = profesionalApi,
            servicio = servicioApi,
            reserva = reservaApi
        )

        composeRule.setThemedContent {
            AppNavigation()
        }

        check(
            com.example.sanaescencia.data.remote.RetrofitClient.usuarioApiService === usuarioApi
        ) { "La inyeccion de la API de usuario fallo" }
    }

    private fun iniciarSesion() {
        composeRule.textField(0).performTextInput("ana@test.cl")
        composeRule.textField(1).performTextInput("password123")
        composeRule.onNodeWithText("Iniciar sesión").performScrollTo().performClick()
        composeRule.waitForIdle()
        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodesWithText("¡Hola, Ana!")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        assertEquals("ana@test.cl", usuarioApi.lastLoginRequest?.correo)
        assertEquals("password123", usuarioApi.lastLoginRequest?.password)
    }

    private fun click(texto: String) {
        composeRule.onNodeWithText(texto).performScrollTo().performClick()
        composeRule.waitForIdle()
    }

    @Test
    fun `login inicio reservas detalle y cancelacion`() {
        iniciarSesion()

        // Inicio: la reserva falsa aparece como proxima sesion.
        composeRule.onNodeWithText("PRÓXIMA RESERVA").assertExists()
        composeRule.onNodeWithText("Ver mi reserva").assertExists()

        // Navegacion inferior a reservas.
        composeRule.onNodeWithText("Reservas").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Mis reservas").assertExists()
        composeRule.onNodeWithText("Ver detalles").assertExists()

        // Detalle de la reserva.
        click("Ver detalles")
        composeRule.onNodeWithText("Detalle de reserva").assertExists()
        composeRule.onNodeWithText("Reserva #0100").assertExists()

        // Cancelacion confirmada.
        click("Cancelar reserva")
        composeRule.onNodeWithText("¿Estás seguro de que deseas cancelar esta reserva?")
            .assertExists()
        composeRule.onNodeWithText("Sí, cancelar").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Reserva cancelada correctamente").assertExists()
        assertEquals(100L, reservaApi.lastCancelarId)

        // Vuelta al listado desde el dialogo de cancelacion.
        composeRule.onNodeWithText("Volver a reservas").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Mis reservas").assertExists()
    }

    @Test
    fun `perfil editar cambiar contrasena y cerrar sesion`() {
        iniciarSesion()

        composeRule.onNodeWithText("Perfil").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Mi perfil").assertExists()
        composeRule.onAllNodesWithText("Ana Pérez").assertCountEquals(2)

        click("Editar información")
        composeRule.onNodeWithText("Actualiza tus datos personales.").assertExists()
        composeRule.onNodeWithText("Datos personales").assertExists()

        composeRule.onNodeWithContentDescription("Volver").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Mi perfil").assertExists()

        click("Cambiar contraseña")
        composeRule.onNodeWithText("Recuperar contraseña").assertExists()

        composeRule.onNodeWithContentDescription("Volver").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Mi perfil").assertExists()

        click("Cerrar sesión")
        composeRule.onNodeWithText("¡Bienvenido!").assertExists()
    }

    @Test
    fun `nueva reserva inicia el pago y confirma la reserva`() {
        iniciarSesion()

        click("Nueva reserva")
        composeRule.onNodeWithText(
            "Selecciona tu atención y encuentra el horario que mejor te acomode."
        ).assertExists()

        click("Psicología")
        click("Dra. Ana Soto")
        click("Terapia individual")
        click("Online")

        click(
            java.time.LocalDate.now().format(
                java.time.format.DateTimeFormatter.ofPattern(
                    "EEE d",
                    java.util.Locale("es", "CL")
                )
            ).replaceFirstChar { it.titlecase() }
        )

        click("09:00")

        click("Continuar y pagar")

        // La pantalla de Webpay se muestra mientras la WebView carga.
        composeRule.onNodeWithText("Conectando con Webpay...").assertExists()

        val webView = encontrarWebView()
        val cliente = webView.webViewClient

        // Una navegacion normal termina el estado de carga.
        cliente.onPageFinished(webView, "https://webpay.test/pagar")
        composeRule.waitForIdle()

        // El retorno del backend dispara la verificacion del pago.
        cliente.onPageFinished(
            webView,
            "https://sanaesencia-app.duckdns.org/api/pagos/webpay/retorno"
        )
        composeRule.waitForIdle()

        // La reserva falsa esta confirmada, por lo que se muestra el dialogo.
        composeRule.onNodeWithText("¡Reserva confirmada!").assertExists()
        composeRule.onNodeWithText("Servicio: Terapia individual").assertExists()

        composeRule.onNodeWithText("Aceptar").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Mis reservas").assertExists()
    }

    private fun encontrarWebView(): WebView {
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
}
