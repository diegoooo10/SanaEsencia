package com.example.sanaescencia.ui.components

import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.sanaescencia.navigation.Routes
import com.example.sanaescencia.testutil.setThemedContent
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ComponentsTest {

    @get:Rule
    val composeRule = createComposeRule()

    private var rutaNavegada: String? = null

    // --- Navbar ---

    private fun renderNavbar(currentRoute: String?) {
        composeRule.setThemedContent {
            Navbar(
                currentRoute = currentRoute,
                onNavigate = { rutaNavegada = it }
            )
        }
    }

    @Test
    fun `la navbar muestra los tres destinos`() {
        renderNavbar(Routes.HOME)

        composeRule.onNodeWithText("Inicio").assertExists()
        composeRule.onNodeWithText("Reservas").assertExists()
        composeRule.onNodeWithText("Perfil").assertExists()
        composeRule.onNodeWithContentDescription("Inicio", useUnmergedTree = true).assertExists()
        composeRule.onNodeWithContentDescription("Reservas", useUnmergedTree = true).assertExists()
        composeRule.onNodeWithContentDescription("Perfil", useUnmergedTree = true).assertExists()
    }

    @Test
    fun `la navbar marca el destino actual como seleccionado`() {
        renderNavbar(Routes.RESERVATIONS)

        composeRule.onNodeWithText("Reservas").assertIsSelected()
        composeRule.onNodeWithText("Inicio").assertIsNotSelected()
        composeRule.onNodeWithText("Perfil").assertIsNotSelected()
    }

    @Test
    fun `sin ruta actual ningun destino esta seleccionado`() {
        renderNavbar(null)

        composeRule.onNodeWithText("Inicio").assertIsNotSelected()
        composeRule.onNodeWithText("Reservas").assertIsNotSelected()
        composeRule.onNodeWithText("Perfil").assertIsNotSelected()
    }

    @Test
    fun `cada item navega a su ruta`() {
        renderNavbar(Routes.HOME)

        composeRule.onNodeWithText("Reservas").performClick()
        assertEquals(Routes.RESERVATIONS, rutaNavegada)

        composeRule.onNodeWithText("Perfil").performClick()
        assertEquals(Routes.PROFILE, rutaNavegada)

        composeRule.onNodeWithText("Inicio").performClick()
        assertEquals(Routes.HOME, rutaNavegada)
    }

    // --- ReservationCancelledDialog ---

    @Test
    fun `el dialogo de cancelacion muestra su mensaje y navega`() {
        var volverCalls = 0
        composeRule.setThemedContent {
            ReservationCancelledDialog(onVolverReservas = { volverCalls++ })
        }

        composeRule.onNodeWithText("Reserva cancelada correctamente").assertExists()
        composeRule.onNodeWithText(
            "Tu reserva fue cancelada correctamente y el horario vuelve a estar disponible. Puedes agendar una nueva atención cuando quieras."
        ).assertExists()
        composeRule.onNodeWithContentDescription("Logo SanaEsencia").assertExists()

        composeRule.onNodeWithText("Volver a reservas").performClick()

        assertEquals(1, volverCalls)
    }

    // --- ReservationConfirmationDialog ---

    @Test
    fun `el dialogo de confirmacion muestra los datos de la reserva`() {
        composeRule.setThemedContent {
            ReservationConfirmationDialog(
                service = "Terapia individual",
                professional = "Dra. Ana Soto",
                date = "2026-12-01",
                hour = "10:00",
                modality = "Online",
                onDismiss = {},
                onConfirm = {}
            )
        }

        composeRule.onNodeWithText("¡Reserva confirmada!").assertExists()
        composeRule.onNodeWithText(
            "Tu pago fue aprobado y tu reserva ha sido confirmada correctamente."
        ).assertExists()
        composeRule.onNodeWithText("Servicio: Terapia individual").assertExists()
        composeRule.onNodeWithText("Profesional: Dra. Ana Soto").assertExists()
        composeRule.onNodeWithText("Fecha: 2026-12-01").assertExists()
        composeRule.onNodeWithText("Hora: 10:00").assertExists()
        composeRule.onNodeWithText("Modalidad: Online").assertExists()
    }

    @Test
    fun `el boton aceptar confirma la reserva`() {
        var confirmCalls = 0
        composeRule.setThemedContent {
            ReservationConfirmationDialog(
                service = "Servicio",
                professional = "Profesional",
                date = "Fecha",
                hour = "Hora",
                modality = "Modalidad",
                onDismiss = {},
                onConfirm = { confirmCalls++ }
            )
        }

        composeRule.onNodeWithText("Aceptar").performClick()

        assertEquals(1, confirmCalls)
    }

    // --- CameraScreen ---

    @Test
    fun `sin permiso la camara muestra el aviso y permite volver`() {
        var closeCalls = 0
        composeRule.setThemedContent {
            CameraScreen(
                onPhotoCaptured = {},
                onClose = { closeCalls++ }
            )
        }

        composeRule.onNodeWithText("Necesitamos permiso para utilizar la cámara.").assertExists()
        composeRule.onNodeWithText("Tomar fotografía").assertDoesNotExist()

        composeRule.onNodeWithText("Volver").performClick()

        assertEquals(1, closeCalls)
    }
}
