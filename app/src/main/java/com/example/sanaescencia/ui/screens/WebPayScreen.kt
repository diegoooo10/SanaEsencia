
package com.example.sanaescencia.ui.screens

import android.graphics.Bitmap
import android.net.Uri
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.delay
import com.example.sanaescencia.R


@Composable
fun WebpayScreen(
    url: String,
    token: String,
    estadoFinal: String?,
    onPagoFinalizado: () -> Unit,
    onVolverReservas: () -> Unit
) {
    val context = LocalContext.current
    if (estadoFinal != null) {
        ResultadoReservaScreen(
            estado = estadoFinal,
            onVolverReservas = onVolverReservas
        )
        return
    }

    var retornoDetectado by remember { mutableStateOf(false) }
    var cargando by remember { mutableStateOf(true) }
    var procesandoRetorno by remember { mutableStateOf(false) }
    var mensajeError by remember { mutableStateOf<String?>(null) }

    val webView = remember { WebView(context) }

    // Limita la espera mientras se procesa el retorno de Webpay.
    LaunchedEffect(procesandoRetorno, retornoDetectado) {
        if (procesandoRetorno && !retornoDetectado) {
            delay(30_000)

            if (procesandoRetorno && !retornoDetectado && mensajeError == null) {
                mensajeError = "La verificación está tardando más de lo esperado."
                procesandoRetorno = false
                cargando = false
            }
        }
    }

    // Libera los recursos de la WebView al salir de la pantalla.
    DisposableEffect(webView) {
        onDispose {
            webView.stopLoading()
            webView.destroy()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .alpha(if (cargando || procesandoRetorno || mensajeError != null) 0f else 1f),
            factory = {
                webView.apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true

                    webViewClient = object : WebViewClient() {

                        // Detecta cuando comienza el retorno hacia nuestro backend.
                        override fun onPageStarted(view: WebView?, urlActual: String?, favicon: Bitmap?) {
                            super.onPageStarted(view, urlActual, favicon)

                            if (esRetorno(urlActual)) {
                                procesandoRetorno = true
                            }
                        }

                        // Finaliza la navegación cuando el backend responde correctamente.
                        override fun onPageFinished(view: WebView?, urlActual: String?) {
                            super.onPageFinished(view, urlActual)

                            if (esRetorno(urlActual)) {
                                if (!retornoDetectado && mensajeError == null) {
                                    retornoDetectado = true
                                    procesandoRetorno = true
                                    onPagoFinalizado()
                                }
                                return
                            }

                            if (!procesandoRetorno && mensajeError == null
                                && urlActual?.startsWith("https://") == true) {
                                cargando = false
                            }
                        }

                        // Maneja los errores HTTP de la página principal.
                        override fun onReceivedHttpError(
                            view: WebView?,
                            request: WebResourceRequest?,
                            errorResponse: WebResourceResponse?
                        ) {
                            super.onReceivedHttpError(view, request, errorResponse)

                            if (request?.isForMainFrame == true) {
                                if (esRetorno(request.url.toString())) {
                                    if (!retornoDetectado) {
                                        retornoDetectado = true
                                        procesandoRetorno = true
                                        onPagoFinalizado()
                                    }
                                } else {
                                    mensajeError = "No pudimos cargar la página de pago."
                                    procesandoRetorno = false
                                    cargando = false
                                }
                            }
                        }

                        // Maneja los errores de conexión de la página principal.
                        override fun onReceivedError(
                            view: WebView?,
                            request: WebResourceRequest?,
                            error: WebResourceError?
                        ) {
                            super.onReceivedError(view, request, error)

                            if (request?.isForMainFrame == true) {
                                mensajeError = "No se pudo conectar con el servicio de pago."
                                procesandoRetorno = false
                                cargando = false
                            }
                        }
                    }

                    val formulario = """
                        <html>
                        <body onload="document.forms[0].submit()">
                            <form method="POST" action="$url">
                                <input type="hidden" name="token_ws" value="$token"/>
                            </form>
                        </body>
                        </html>
                    """.trimIndent()

                    loadDataWithBaseURL(null, formulario, "text/html", "UTF-8", null)
                }
            }
        )

        if (mensajeError != null) {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = mensajeError.orEmpty(),
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "El resultado de tu pago aún no está confirmado. Puedes consultar tu reserva.",
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                Button(onClick = {
                    if (!retornoDetectado) {
                        retornoDetectado = true
                        onPagoFinalizado()
                    }
                }) {
                    Text("Consultar reserva")
                }
            }
        } else if (cargando || procesandoRetorno) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    color = androidx.compose.ui.graphics.Color(0xFF617A56)
                )

                Text(
                    text = if (procesandoRetorno) {
                        "Verificando tu pago..."
                    } else {
                        "Conectando con Webpay..."
                    },
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(top = 20.dp)
                )

                Text(
                    text = if (procesandoRetorno) {
                        "Estamos consultando el resultado de tu transacción."
                    } else {
                        "Estamos preparando tu pago seguro."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

// Comprueba que la navegación corresponde al retorno de Webpay.
private fun esRetorno(url: String?): Boolean {
    return url?.let {
        Uri.parse(it).path == "/api/pagos/webpay/retorno"
    } == true


}

@Composable
private fun ResultadoReservaScreen(
    estado: String,
    onVolverReservas: () -> Unit
) {
    val titulo = when (estado) {
        "NO_CONCRETADA" -> "Proceso de reserva anulado"
        "EXPIRADA" -> "El tiempo de reserva terminó"
        else -> "Reserva cancelada"
    }

    val descripcion = when (estado) {
        "NO_CONCRETADA" ->
            "Tu reserva no se realizó y el horario vuelve a estar disponible. Puedes agendar una nueva atención cuando quieras."

        "EXPIRADA" ->
            "No alcanzaste a completar tu reserva. Puedes seleccionar un nuevo horario e intentarlo nuevamente."

        else ->
            "Tu reserva fue cancelada. Puedes volver a reservas para agendar una nueva atención."
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(androidx.compose.ui.graphics.Color(0xFFF9F8F6))
            .padding(28.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.logo_header),
            contentDescription = "Logo SanaEsencia",
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .offset(x = (-10).dp),
            contentScale = ContentScale.Fit
        )

        Text(
            text = titulo,
            style = MaterialTheme.typography.headlineSmall,
            color = androidx.compose.ui.graphics.Color(0xFF344C31),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(top = 36.dp)
        )

        Text(
            text = descripcion,
            style = MaterialTheme.typography.bodyLarge,
            color = androidx.compose.ui.graphics.Color(0xFF52634E),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp, bottom = 36.dp)
        )

        Button(
            onClick = onVolverReservas,
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = androidx.compose.ui.graphics.Color(0xFF617A56)
            )
        ) {
            Text("Volver a reservas")
        }
    }
}
