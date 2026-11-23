package com.example.sanaescencia.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ThemeTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `el tema aplica los colores de la marca`() {
        var primario: Color? = null
        var secundario: Color? = null
        var terciario: Color? = null
        var fondo: Color? = null
        var superficie: Color? = null

        composeRule.setContent {
            SanaEscenciaTheme {
                primario = MaterialTheme.colorScheme.primary
                secundario = MaterialTheme.colorScheme.secondary
                terciario = MaterialTheme.colorScheme.tertiary
                fondo = MaterialTheme.colorScheme.background
                superficie = MaterialTheme.colorScheme.surface
                Text("contenido del tema")
            }
        }

        assertEquals(Purple40, primario)
        assertEquals(PurpleGrey40, secundario)
        assertEquals(Pink40, terciario)
        assertEquals(Color(0xFFF9F8F6), fondo)
        assertEquals(Color(0xFFF9F8F6), superficie)
        composeRule.onNodeWithText("contenido del tema").assertExists()
    }

    @Test
    fun `las constantes de color conservan sus valores`() {
        assertEquals(Color(0xFFD0BCFF), Purple80)
        assertEquals(Color(0xFFCCC2DC), PurpleGrey80)
        assertEquals(Color(0xFFEFB8C8), Pink80)
        assertEquals(Color(0xFF6650a4), Purple40)
        assertEquals(Color(0xFF625b71), PurpleGrey40)
        assertEquals(Color(0xFF7D5260), Pink40)
    }
}
