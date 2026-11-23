package com.example.sanaescencia.navigation

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.example.sanaescencia.testutil.setThemedContent
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AppNavigationTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private var recoveryToken by mutableStateOf<String?>(null)

    private fun render() {
        composeRule.setThemedContent {
            AppNavigation(recoveryToken = recoveryToken)
        }
    }

    private fun entregarToken(token: String?) {
        composeRule.runOnIdle { recoveryToken = token }
        composeRule.waitForIdle()
    }

    @Test
    fun `la aplicacion inicia en la pantalla de login`() {
        render()

        composeRule.onNodeWithText("¡Bienvenido!").assertExists()
        composeRule.onNodeWithText("Iniciar sesión").assertExists()
        composeRule.onNodeWithText("Registrarse").assertExists()
        composeRule.onNodeWithText("Reservas").assertDoesNotExist()
    }

    @Test
    fun `desde login se navega al registro y se vuelve`() {
        render()

        composeRule.onNodeWithText("Registrarse").performScrollTo().performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Completa tus datos para registrarte.").assertExists()
        composeRule.onNodeWithText("Datos de acceso").assertExists()

        composeRule.onNodeWithText("Iniciar sesión").performScrollTo().performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("¡Bienvenido!").assertExists()
    }

    @Test
    fun `desde login se navega a recuperar contrasena y se vuelve`() {
        render()

        composeRule.onNodeWithText("¿Olvidaste tu contraseña?")
            .performScrollTo()
            .performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Recuperar contraseña").assertExists()
        composeRule.onNodeWithText("Enviar enlace de recuperación").assertExists()

        composeRule.onNodeWithContentDescription("Volver").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("¡Bienvenido!").assertExists()
    }

    @Test
    fun `un token de recuperacion abre la pantalla de nueva contrasena`() {
        render()

        entregarToken("token-de-prueba")

        composeRule.onNodeWithText("Guardar nueva contraseña").assertExists()
        composeRule.onNodeWithText("Seguridad de la cuenta").assertExists()
    }

    @Test
    fun `un token de recuperacion en blanco no navega`() {
        render()

        entregarToken("   ")

        composeRule.onNodeWithText("¡Bienvenido!").assertExists()
        composeRule.onNodeWithText("Guardar nueva contraseña").assertDoesNotExist()
    }

    @Test
    fun `desde nueva contrasena se puede volver al login`() {
        render()

        entregarToken("otro-token")
        composeRule.onNodeWithText("Guardar nueva contraseña").assertExists()

        composeRule.onNodeWithContentDescription("Volver").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("¡Bienvenido!").assertExists()
    }
}
