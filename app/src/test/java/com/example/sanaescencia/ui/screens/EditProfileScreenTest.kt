package com.example.sanaescencia.ui.screens

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import com.example.sanaescencia.data.remote.dto.UsuarioUpdateRequest
import com.example.sanaescencia.testutil.TestData
import com.example.sanaescencia.testutil.setThemedContent
import com.example.sanaescencia.testutil.textField
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EditProfileScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private var backCalls = 0
    private var request: UsuarioUpdateRequest? = null

    private fun render(
        usuario: com.example.sanaescencia.data.remote.dto.UsuarioResponse? = TestData.usuarioResponse,
        isLoading: Boolean = false,
        error: String? = null
    ) {
        composeRule.setThemedContent {
            EditProfileScreen(
                usuario = usuario,
                isLoading = isLoading,
                error = error,
                onBack = { backCalls++ },
                onSave = { request = it }
            )
        }
    }

    @Test
    fun `sin usuario los campos de solo lectura muestran no disponible`() {
        render(usuario = null)

        composeRule.onNodeWithText("Editar información").assertExists()
        composeRule.onNodeWithText("Actualiza tus datos personales.").assertExists()
        composeRule.onAllNodesWithText("No disponible").assertCountEquals(3)
    }

    @Test
    fun `muestra los datos actuales del usuario`() {
        render()

        composeRule.onNodeWithText("Ana").assertExists()
        composeRule.onNodeWithText("Pérez").assertExists()
        composeRule.onNodeWithText("10-05-1990").assertExists()
        composeRule.onNodeWithText("ana@test.cl").assertExists()
        composeRule.onNodeWithText("+56 9 12345678").assertExists()
        composeRule.onNodeWithText("Av. Siempre Viva 123").assertExists()
    }

    @Test
    fun `sin usuario el boton guardar esta deshabilitado`() {
        render(usuario = null)

        composeRule.onNodeWithText("Guardar cambios").assertIsNotEnabled()
    }

    @Test
    fun `con usuario el boton guardar esta habilitado`() {
        render()

        composeRule.onNodeWithText("Guardar cambios").assertIsEnabled()
    }

    @Test
    fun `al guardar envia los campos recortados`() {
        render()
        composeRule.textField(1).performTextClearance()
        composeRule.textField(1).performTextInput("  99999999  ")
        composeRule.textField(2).performTextClearance()
        composeRule.textField(2).performTextInput("  Nueva dirección  ")

        composeRule.onNodeWithText("Guardar cambios").performScrollTo().performClick()

        assertEquals("ana@test.cl", request?.correo)
        assertEquals("99999999", request?.telefono)
        assertEquals("Nueva dirección", request?.direccion)
    }

    @Test
    fun `se puede vaciar la direccion`() {
        render()
        composeRule.textField(2).performTextClearance()

        composeRule.onNodeWithText("Guardar cambios").performScrollTo().performClick()

        assertEquals("", request?.direccion)
    }

    @Test
    fun `muestra el mensaje de error recibido`() {
        render(error = "No se pudo actualizar el perfil")

        composeRule.onNodeWithText("No se pudo actualizar el perfil").assertExists()
    }

    @Test
    fun `mientras carga deshabilita el boton y oculta su texto`() {
        render(isLoading = true)

        composeRule.onNodeWithText("Guardar cambios").assertDoesNotExist()
    }

    @Test
    fun `el boton volver dispara onBack`() {
        render()

        composeRule.onNodeWithContentDescription("Volver").performClick()

        assertEquals(1, backCalls)
    }

    @Test
    fun `sin usuario el guardado no envia request`() {
        render(usuario = null)

        composeRule.onNodeWithText("Guardar cambios").performClick()

        assertNull(request)
    }
}
