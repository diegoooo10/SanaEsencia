package com.example.sanaescencia.testutil

import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import com.example.sanaescencia.ui.theme.SanaEscenciaTheme
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response

fun httpException(code: Int): HttpException =
    HttpException(
        Response.error<Any>(
            code,
            """{"message":"error $code"}""".toResponseBody("application/json".toMediaType())
        )
    )

/** Compone contenido dentro del tema real de la aplicacion. */
fun ComposeContentTestRule.setThemedContent(content: @androidx.compose.runtime.Composable () -> Unit) {
    setContent {
        SanaEscenciaTheme {
            content()
        }
    }
}

/** Devuelve el campo de texto editable en la posicion indicada (orden de composicion). */
fun ComposeContentTestRule.textField(index: Int) =
    onAllNodes(hasSetTextAction())[index]
