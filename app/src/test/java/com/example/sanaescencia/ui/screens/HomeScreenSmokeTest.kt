package com.example.sanaescencia.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.sanaescencia.data.model.Reserva
import com.example.sanaescencia.testutil.TestData
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class HomeScreenSmokeTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun muestraBienvenidaYProximaReserva() {
        val reserva: Reserva = TestData.reserva.copy(fecha = LocalDate.now().plusDays(2))

        composeRule.setContent {
            HomeScreen(
                nombreUsuario = "Ana",
                reservas = listOf(reserva)
            )
        }

        composeRule.onNodeWithText("¡Hola, Ana!").assertIsDisplayed()
        composeRule.onNodeWithText("Terapia individual").assertIsDisplayed()
    }
}
