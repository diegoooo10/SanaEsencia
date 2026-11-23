package com.example.sanaescencia.ui.screens

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import com.example.sanaescencia.testutil.FakeUsuarioRepository
import com.example.sanaescencia.testutil.httpException
import com.example.sanaescencia.testutil.setThemedContent
import com.example.sanaescencia.testutil.textField
import com.example.sanaescencia.ui.viewmodel.UsuarioViewModel
import kotlinx.coroutines.CompletableDeferred
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
class LoginScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val repository = FakeUsuarioRepository()
    private val viewModel = UsuarioViewModel(repository)

    private var loginCalls = 0
    private var registerCalls = 0
    private var forgotCalls = 0

    private fun render() {
        composeRule.setThemedContent {
            LoginScreen(
                viewModel = viewModel,
                onLogin = { loginCalls++ },
                onRegister = { registerCalls++ },
                onForgotPassword = { forgotCalls++ }
            )
        }
    }

    private fun llenarCredenciales(correo: String = "ana@test.cl", password: String = "password123") {
        composeRule.textField(0).performTextInput(correo)
        composeRule.textField(1).performTextInput(password)
    }

    private fun botonLogin() = composeRule.onNodeWithText("Iniciar sesión")

    @Test
    fun `muestra los elementos principales`() {
        render()

        composeRule.onNodeWithText("¡Bienvenido!").assertExists()
        composeRule.onNodeWithText("Ingresa a tu cuenta para continuar.").assertExists()
        composeRule.onNodeWithText("Correo electrónico").assertExists()
        composeRule.onNodeWithText("Contraseña").assertExists()
        composeRule.onNodeWithText("Registrarse").assertExists()
    }

    @Test
    fun `el boton iniciar sesion esta deshabilitado hasta completar ambos campos`() {
        render()

        botonLogin().assertIsNotEnabled()

        composeRule.textField(0).performTextInput("ana@test.cl")
        botonLogin().assertIsNotEnabled()

        composeRule.textField(1).performTextInput("password123")
        botonLogin().assertIsEnabled()
    }

    @Test
    fun `alterna la visibilidad de la contrasena`() {
        render()

        composeRule.onNodeWithContentDescription("Mostrar contraseña").performClick()
        composeRule.onNodeWithContentDescription("Ocultar contraseña").assertExists()

        composeRule.onNodeWithContentDescription("Ocultar contraseña").performClick()
        composeRule.onNodeWithContentDescription("Mostrar contraseña").assertExists()
    }

    @Test
    fun `navega al registro`() {
        render()

        composeRule.onNodeWithText("Registrarse").performScrollTo().performClick()

        assertEquals(1, registerCalls)
    }

    @Test
    fun `navega a recuperar contrasena`() {
        render()

        composeRule.onNodeWithText("¿Olvidaste tu contraseña?").performScrollTo().performClick()

        assertEquals(1, forgotCalls)
    }

    @Test
    fun `un login exitoso dispara onLogin`() {
        render()
        llenarCredenciales()

        botonLogin().performScrollTo().performClick()
        composeRule.waitForIdle()

        assertEquals(1, loginCalls)
        assertEquals("ana@test.cl", repository.lastLoginRequest?.correo)
        assertEquals("password123", repository.lastLoginRequest?.password)
    }

    @Test
    fun `credenciales incorrectas muestran el mensaje de error 401`() {
        repository.iniciarSesionResult = Result.failure(httpException(401))
        render()
        llenarCredenciales()

        botonLogin().performScrollTo().performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("El correo o la contraseña son incorrectos.").assertExists()
        assertEquals(0, loginCalls)
    }

    @Test
    fun `un error 403 muestra falta de autorizacion`() {
        repository.iniciarSesionResult = Result.failure(httpException(403))
        render()
        llenarCredenciales()

        botonLogin().performScrollTo().performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("No tienes autorización para acceder.").assertExists()
    }

    @Test
    fun `un error 5xx muestra el mensaje de servicio no disponible`() {
        repository.iniciarSesionResult = Result.failure(httpException(503))
        render()
        llenarCredenciales()

        botonLogin().performScrollTo().performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText(
            "No pudimos iniciar sesión en este momento. Inténtalo más tarde."
        ).assertExists()
    }

    @Test
    fun `un error HTTP distinto muestra el mensaje generico`() {
        repository.iniciarSesionResult = Result.failure(httpException(418))
        render()
        llenarCredenciales()

        botonLogin().performScrollTo().performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("No pudimos iniciar sesión. Inténtalo nuevamente.")
            .assertExists()
    }

    @Test
    fun `un error de conexion muestra el mensaje de red`() {
        repository.iniciarSesionResult = Result.failure(IOException("sin red"))
        render()
        llenarCredenciales()

        botonLogin().performScrollTo().performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("No pudimos conectarnos. Revisa tu conexión a internet.")
            .assertExists()
    }

    @Test
    fun `un error desconocido muestra el mensaje generico`() {
        repository.iniciarSesionResult = Result.failure(IllegalStateException("raro"))
        render()
        llenarCredenciales()

        botonLogin().performScrollTo().performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText(
            "No pudimos iniciar sesión en este momento. Inténtalo nuevamente."
        ).assertExists()
    }

    @Test
    fun `mientras carga muestra el indicador y deshabilita el boton`() {
        val gate = CompletableDeferred<Unit>()
        repository.gate = gate
        render()
        llenarCredenciales()

        botonLogin().performScrollTo().performClick()
        composeRule.waitForIdle()

        assertTrue(viewModel.isLoading.value)
        botonLogin().assertDoesNotExist()
        assertEquals(0, loginCalls)

        gate.complete(Unit)
        composeRule.waitForIdle()
        assertEquals(1, loginCalls)
        assertFalse(viewModel.isLoading.value)
    }
}
