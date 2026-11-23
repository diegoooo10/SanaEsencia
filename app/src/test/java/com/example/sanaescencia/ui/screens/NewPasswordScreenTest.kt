package com.example.sanaescencia.ui.screens

import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
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
class NewPasswordScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private var backCalls = 0
    private var passwordGuardada: String? = null

    private fun render(
        restablecimientoState: Result<Unit>? = null,
        isLoading: Boolean = false
    ) {
        composeRule.setThemedContent {
            NewPasswordScreen(
                onBack = { backCalls++ },
                onSave = { passwordGuardada = it },
                restablecimientoState = restablecimientoState,
                isLoading = isLoading
            )
        }
    }

    private fun botonGuardar() =
        composeRule.onNodeWithText("Guardar nueva contraseña")

    @Test
    fun `muestra el encabezado y los campos`() {
        render()

        composeRule.onNodeWithText("Establece una nueva contraseña para tu cuenta.").assertExists()
        composeRule.onNodeWithText("Seguridad de la cuenta").assertExists()
        composeRule.onNodeWithText("Debe contener al menos 8 caracteres.").assertExists()
        composeRule.onNodeWithText("Confirmar contraseña").assertExists()
        composeRule.onAllNodesWithText("Nueva contraseña").assertCountEquals(2)
        botonGuardar().assertExists()
    }

    @Test
    fun `sin contrasena muestra error`() {
        render()

        botonGuardar().performClick()

        composeRule.onNodeWithText("Ingresa tu nueva contraseña.").assertExists()
        assertNull(passwordGuardada)
    }

    @Test
    fun `una contrasena corta muestra error`() {
        render()
        composeRule.textField(0).performTextInput("corta")

        botonGuardar().performClick()

        composeRule.onNodeWithText("La contraseña debe tener al menos 8 caracteres.").assertExists()
        assertNull(passwordGuardada)
    }

    @Test
    fun `sin confirmacion muestra error`() {
        render()
        composeRule.textField(0).performTextInput("password123")

        botonGuardar().performClick()

        composeRule.onNodeWithText("Confirma tu nueva contraseña.").assertExists()
        assertNull(passwordGuardada)
    }

    @Test
    fun `contrasenas distintas muestran error`() {
        render()
        composeRule.textField(0).performTextInput("password123")
        composeRule.textField(1).performTextInput("password456")

        botonGuardar().performClick()

        composeRule.onNodeWithText("Las contraseñas no coinciden.").assertExists()
        assertNull(passwordGuardada)
    }

    @Test
    fun `contrasenas validas guardan el valor`() {
        render()
        composeRule.textField(0).performTextInput("password123")
        composeRule.textField(1).performTextInput("password123")

        botonGuardar().performClick()

        assertEquals("password123", passwordGuardada)
    }

    @Test
    fun `escribir limpia el error previo`() {
        render()
        botonGuardar().performClick()
        composeRule.onNodeWithText("Ingresa tu nueva contraseña.").assertExists()

        composeRule.textField(0).performTextInput("x")

        composeRule.onNodeWithText("Ingresa tu nueva contraseña.").assertDoesNotExist()
    }

    @Test
    fun `alterna la visibilidad de ambas contrasenas`() {
        render()

        composeRule.onAllNodesWithText("Mostrar")[0].performClick()
        composeRule.onAllNodesWithText("Ocultar")[0].assertExists()

        composeRule.onAllNodesWithText("Mostrar")[0].performClick()
        composeRule.onAllNodesWithText("Ocultar")[0].assertExists()
        composeRule.onAllNodesWithText("Ocultar")[1].assertExists()

        composeRule.onAllNodesWithText("Ocultar")[1].performClick()
        composeRule.onAllNodesWithText("Mostrar")[0].assertExists()
    }

    @Test
    fun `un resultado exitoso muestra la confirmacion`() {
        render(restablecimientoState = Result.success(Unit))

        composeRule.onNodeWithText("Tu contraseña fue actualizada correctamente.").assertExists()
    }

    @Test
    fun `con exito el boton queda deshabilitado`() {
        render(restablecimientoState = Result.success(Unit))

        botonGuardar().assertIsNotEnabled()
    }

    @Test
    fun `un resultado fallido muestra el mensaje de error`() {
        render(restablecimientoState = Result.failure(IllegalStateException("token vencido")))

        composeRule.onNodeWithText(
            "No fue posible restablecer la contraseña. El enlace puede ser inválido o haber expirado."
        ).assertExists()
    }

    @Test
    fun `mientras carga oculta el texto del boton`() {
        render(isLoading = true)

        botonGuardar().assertDoesNotExist()
    }

    @Test
    fun `el boton volver dispara onBack`() {
        render()

        composeRule.onNodeWithContentDescription("Volver").performClick()

        assertEquals(1, backCalls)
    }
}
