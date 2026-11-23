package com.example.sanaescencia.ui.screens

import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.assertIsEnabled
import com.example.sanaescencia.testutil.FakeUsuarioRepository
import com.example.sanaescencia.testutil.setThemedContent
import com.example.sanaescencia.testutil.textField
import com.example.sanaescencia.ui.viewmodel.UsuarioViewModel
import kotlinx.coroutines.CompletableDeferred
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RegisterScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val repository = FakeUsuarioRepository()
    private val viewModel = UsuarioViewModel(repository)

    private var loginCalls = 0

    private fun render() {
        composeRule.setThemedContent {
            RegisterScreen(
                viewModel = viewModel,
                onLogin = { loginCalls++ }
            )
        }
    }

    private fun botonCrear() =
        composeRule.onNode(hasText("Crear cuenta") and hasClickAction())

    private fun llenarFormulario(
        nombre: String = "Ana",
        apellido: String = "Pérez",
        telefono: String = "12345678",
        fecha: String = "01011990",
        ciudad: String = "",
        correo: String = "ana@test.cl",
        password: String = "password123",
        confirmacion: String = "password123"
    ) {
        composeRule.textField(0).performTextInput(nombre)
        composeRule.textField(1).performTextInput(apellido)
        composeRule.textField(2).performTextInput(telefono)
        composeRule.textField(3).performTextInput(fecha)
        if (ciudad.isNotEmpty()) composeRule.textField(4).performTextInput(ciudad)
        composeRule.textField(5).performTextInput(correo)
        composeRule.textField(6).performTextInput(password)
        composeRule.textField(7).performTextInput(confirmacion)
    }

    @Test
    fun `muestra el formulario completo`() {
        render()

        botonCrear().assertExists()
        composeRule.onNodeWithText("Completa tus datos para registrarte.").assertExists()
        composeRule.onNodeWithText("Datos de acceso").assertExists()
        composeRule.onNodeWithText("Nombre").assertExists()
        composeRule.onNodeWithText("Apellido").assertExists()
        composeRule.onNodeWithText("Teléfono").assertExists()
        composeRule.onNodeWithText("Fecha de nacimiento").assertExists()
        composeRule.onNodeWithText("Correo electrónico").assertExists()
        composeRule.onNodeWithText("Confirmar contraseña").assertExists()
    }

    @Test
    fun `el telefono solo acepta digitos y se limita a ocho`() {
        render()

        composeRule.textField(2).performTextInput("abc123456789")

        composeRule.onNodeWithText("12345678").assertExists()
    }

    @Test
    fun `la fecha se formatea automaticamente`() {
        render()

        composeRule.textField(3).performTextInput("01011990")

        composeRule.onNodeWithText("01-01-1990").assertExists()
    }

    @Test
    fun `un formulario vacio muestra todos los errores de validacion`() {
        render()

        botonCrear().performScrollTo().performClick()

        composeRule.onNodeWithText("Ingresa tu nombre y apellido.").assertExists()
        composeRule.onNodeWithText("Ingresa los 8 dígitos de tu teléfono.").assertExists()
        composeRule.onNodeWithText(
            "Ingresa una fecha válida. Debes tener al menos 18 años."
        ).assertExists()
        composeRule.onNodeWithText("Ingresa un correo electrónico válido.").assertExists()
        composeRule.onNodeWithText("La contraseña debe tener al menos 8 caracteres.")
            .assertExists()
        assertEquals(0, repository.registrarCalls)
    }

    @Test
    fun `una fecha de menor de edad es rechazada`() {
        render()
        llenarFormulario(fecha = "01012020")

        botonCrear().performScrollTo().performClick()

        composeRule.onNodeWithText(
            "Ingresa una fecha válida. Debes tener al menos 18 años."
        ).assertExists()
        assertEquals(0, repository.registrarCalls)
    }

    @Test
    fun `contrasenas distintas muestran error`() {
        render()
        llenarFormulario(confirmacion = "otraPassword1")

        botonCrear().performScrollTo().performClick()

        composeRule.onNodeWithText("Las contraseñas no coinciden.").assertExists()
        assertEquals(0, repository.registrarCalls)
    }

    @Test
    fun `un formulario valido registra al usuario con los datos normalizados`() {
        render()
        llenarFormulario(ciudad = "Santiago")

        botonCrear().performScrollTo().performClick()
        composeRule.waitForIdle()

        assertEquals(1, repository.registrarCalls)
        val request = repository.lastRegistrarRequest
        assertEquals("Ana", request?.nombre)
        assertEquals("Pérez", request?.apellido)
        assertEquals("ana@test.cl", request?.correo)
        assertEquals("1990-01-01", request?.fechaNacimiento)
        assertEquals("password123", request?.password)
        assertEquals("Santiago", request?.direccion)
        assertEquals("+56 9 12345678", request?.telefono)
    }

    @Test
    fun `sin ciudad la direccion se envia como null`() {
        render()
        llenarFormulario()

        botonCrear().performScrollTo().performClick()
        composeRule.waitForIdle()

        assertNull(repository.lastRegistrarRequest?.direccion)
    }

    @Test
    fun `un registro exitoso abre el dialogo y navega al login`() {
        render()
        llenarFormulario()

        botonCrear().performScrollTo().performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("¡Cuenta creada!").assertExists()
        composeRule.onNodeWithText("Tu cuenta se creó correctamente.").assertExists()

        composeRule.onNodeWithText("Aceptar").performClick()

        assertEquals(1, loginCalls)
    }

    @Test
    fun `un registro fallido muestra el mensaje del error`() {
        repository.registrarResult = Result.failure(IllegalStateException("correo ya registrado"))
        render()
        llenarFormulario()

        botonCrear().performScrollTo().performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("correo ya registrado").assertExists()
        composeRule.onNodeWithText("¡Cuenta creada!").assertDoesNotExist()
    }

    @Test
    fun `mientras registra el boton se deshabilita`() {
        val gate = CompletableDeferred<Unit>()
        repository.gate = gate
        render()
        llenarFormulario()

        botonCrear().performScrollTo().performClick()
        composeRule.waitForIdle()

        assertTrue(viewModel.isLoading.value)
        botonCrear().assertIsNotEnabled()

        gate.complete(Unit)
        composeRule.waitForIdle()
        botonCrear().assertIsEnabled()
        composeRule.onNodeWithText("¡Cuenta creada!").assertExists()
    }

    @Test
    fun `navega al login desde el enlace inferior`() {
        render()

        composeRule.onNodeWithText("Iniciar sesión").performScrollTo().performClick()

        assertEquals(1, loginCalls)
    }
}
