package com.example.sanaescencia.ui.screens

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.sanaescencia.testutil.setThemedContent
import com.example.sanaescencia.testutil.textField
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ChangePasswordScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private var backCalls = 0
    private var correoEnviado: String? = null

    private fun render(
        recuperacionState: Result<Unit>? = null,
        isLoading: Boolean = false
    ) {
        composeRule.setThemedContent {
            ChangePasswordScreen(
                onBack = { backCalls++ },
                onSendRecovery = { correoEnviado = it },
                recuperacionState = recuperacionState,
                isLoading = isLoading
            )
        }
    }

    private fun botonEnviar() =
        composeRule.onNodeWithText("Enviar enlace de recuperación")

    @Test
    fun `muestra el encabezado y el formulario`() {
        render()

        composeRule.onNodeWithText("Recuperar contraseña").assertExists()
        composeRule.onNodeWithText(
            "Ingresa el correo electrónico asociado a tu cuenta. Te enviaremos un enlace para recuperar tu contraseña."
        ).assertExists()
        composeRule.onNodeWithText("Correo electrónico").assertExists()
        composeRule.onNodeWithText("nombre@correo.cl").assertExists()
        botonEnviar().assertExists()
    }

    @Test
    fun `un correo vacio muestra error y no envia`() {
        render()

        botonEnviar().performClick()

        composeRule.onNodeWithText("Ingresa tu correo electrónico.").assertExists()
        assertNull(correoEnviado)
    }

    @Test
    fun `un correo invalido muestra error y no envia`() {
        render()
        composeRule.textField(0).performTextInput("correo-invalido")

        botonEnviar().performClick()

        composeRule.onNodeWithText("Ingresa un correo electrónico válido.").assertExists()
        assertNull(correoEnviado)
    }

    @Test
    fun `un correo valido envia la solicitud recortada`() {
        render()
        composeRule.textField(0).performTextInput("  ana@test.cl  ")

        botonEnviar().performClick()

        assertEquals("ana@test.cl", correoEnviado)
        composeRule.onNodeWithText("Ingresa un correo electrónico válido.").assertDoesNotExist()
    }

    @Test
    fun `escribir limpia el error previo`() {
        render()
        botonEnviar().performClick()
        composeRule.onNodeWithText("Ingresa tu correo electrónico.").assertExists()

        composeRule.textField(0).performTextInput("a")

        composeRule.onNodeWithText("Ingresa tu correo electrónico.").assertDoesNotExist()
    }

    @Test
    fun `un resultado exitoso muestra la confirmacion`() {
        render(recuperacionState = Result.success(Unit))

        composeRule.onNodeWithText(
            "Si el correo está registrado, recibirás un enlace de recuperación."
        ).assertExists()
    }

    @Test
    fun `un resultado fallido muestra el mensaje de error`() {
        render(recuperacionState = Result.failure(IllegalStateException("sin red")))

        composeRule.onNodeWithText(
            "No fue posible enviar la solicitud. Inténtalo nuevamente."
        ).assertExists()
    }

    @Test
    fun `mientras carga deshabilita el boton y muestra el indicador`() {
        render(isLoading = true)

        botonEnviar().assertDoesNotExist()
        composeRule.onNodeWithText("Enviar enlace de recuperación").assertDoesNotExist()
    }

    @Test
    fun `el boton volver dispara onBack`() {
        render()

        composeRule.onNodeWithContentDescription("Volver").performClick()

        assertEquals(1, backCalls)
    }
}
