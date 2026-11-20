
package com.example.sanaescencia.ui.screens

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.runtime.mutableStateOf
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse

@Composable
fun WebpayScreen(
    url: String,
    token: String,
    onPagoFinalizado: () -> Unit
) {
    val context = LocalContext.current

    val retornoDetectado = remember {
        mutableStateOf(false)
    }

    val retornoConError = remember {
        mutableStateOf(false)
    }

    val webView = remember {
        WebView(context)
    }

    DisposableEffect(webView) {
        onDispose {
            webView.stopLoading()
            webView.destroy()
        }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = {
            webView.apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true

                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(
                        view: WebView?,
                        urlActual: String?
                    ) {
                        super.onPageFinished(view, urlActual)

                        if (
                            !retornoDetectado.value &&
                            !retornoConError.value &&
                            urlActual?.contains("/api/pagos/webpay/retorno") == true
                        ) {
                            retornoDetectado.value = true
                            onPagoFinalizado()
                        }
                    }



                    override fun onReceivedHttpError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        errorResponse: WebResourceResponse?
                    ) {
                        super.onReceivedHttpError(view, request, errorResponse)

                        if (
                            request?.isForMainFrame == true &&
                            request.url.path == "/api/pagos/webpay/retorno"
                        ) {
                            retornoConError.value = true

                            android.util.Log.e(
                                "SanaEsenciaWebpay",
                                "Error HTTP en retorno de Webpay: ${errorResponse?.statusCode}"
                            )
                        }
                    }


                }

                val formulario = """
                    <html>
                    <body onload="document.forms[0].submit()">
                        <form method="POST" action="$url">
                            <input type="hidden"
                                   name="token_ws"
                                   value="$token" />
                        </form>
                    </body>
                    </html>
                """.trimIndent()

                loadDataWithBaseURL(
                    null,
                    formulario,
                    "text/html",
                    "UTF-8",
                    null
                )
            }
        }
    )
}
