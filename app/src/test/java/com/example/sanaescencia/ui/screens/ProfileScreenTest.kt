package com.example.sanaescencia.ui.screens

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTouchInput
import com.example.sanaescencia.testutil.TestData
import com.example.sanaescencia.testutil.setThemedContent
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ProfileScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private var editCalls = 0
    private var passwordCalls = 0
    private var logoutCalls = 0

    private fun render(usuario: com.example.sanaescencia.data.remote.dto.UsuarioResponse? = TestData.usuarioResponse) {
        composeRule.setThemedContent {
            ProfileScreen(
                usuario = usuario,
                onEditProfile = { editCalls++ },
                onChangePassword = { passwordCalls++ },
                onLogout = { logoutCalls++ }
            )
        }
    }

    @Test
    fun `sin usuario muestra los valores por defecto`() {
        render(usuario = null)

        composeRule.onNodeWithText("Mi perfil").assertExists()
        composeRule.onNodeWithText("Cargando perfil...").assertExists()
        composeRule.onNodeWithText("Cliente Sanaesencia").assertExists()
        composeRule.onAllNodesWithText("No disponible").assertCountEquals(3)
        composeRule.onNodeWithText("No registrado").assertExists()
        composeRule.onNodeWithText("No registrada").assertExists()
    }

    @Test
    fun `muestra los datos del usuario`() {
        render()

        composeRule.onAllNodesWithText("Ana Pérez").assertCountEquals(2)
        composeRule.onNodeWithText("ana@test.cl").assertExists()
        composeRule.onNodeWithText("+56 9 12345678").assertExists()
        composeRule.onNodeWithText("Av. Siempre Viva 123").assertExists()
        composeRule.onNodeWithText("10-05-1990").assertExists()
        composeRule.onNodeWithText("Información personal").assertExists()
        composeRule.onNodeWithText("Cuenta").assertExists()
    }

    @Test
    fun `una fecha con formato invalido muestra no disponible`() {
        render(usuario = TestData.usuarioResponse.copy(fechaNacimiento = "1990"))

        composeRule.onNodeWithText("No disponible").assertExists()
    }

    @Test
    fun `editar informacion dispara el callback`() {
        render()

        composeRule.onNodeWithText("Editar información").performScrollTo().performClick()

        assertEquals(1, editCalls)
    }

    @Test
    fun `cambiar contrasena dispara el callback`() {
        render()

        composeRule.onNodeWithText("Cambiar contraseña").performScrollTo().performClick()

        assertEquals(1, passwordCalls)
    }

    @Test
    fun `cerrar sesion dispara el callback`() {
        render()

        composeRule.onNodeWithText("Cerrar sesión").performScrollTo().performClick()

        assertEquals(1, logoutCalls)
    }

    @Test
    fun `al pulsar el avatar se abre la camara y se puede volver`() {
        render()

        composeRule.onNodeWithContentDescription("Perfil").performTouchInput { click() }
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Necesitamos permiso para utilizar la cámara.").assertExists()

        composeRule.onNodeWithText("Volver").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Mi perfil").assertExists()
    }

    @Test
    fun `el icono de perfil se muestra cuando no hay fotografia`() {
        render()

        composeRule.onNodeWithContentDescription("Perfil").assertExists()
        composeRule.onNodeWithText("Eliminar fotografía").assertDoesNotExist()
    }
}
