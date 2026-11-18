
package com.example.sanaescencia

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import androidx.core.view.WindowCompat

import com.example.sanaescencia.navigation.AppNavigation
import com.example.sanaescencia.ui.theme.SanaEscenciaTheme

class MainActivity : ComponentActivity() {

    private val recoveryToken = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recoveryToken.value = obtenerTokenRecuperacion(intent)

        enableEdgeToEdge(
            navigationBarStyle = SystemBarStyle.light(
                android.graphics.Color.rgb(249, 248, 246),
                android.graphics.Color.rgb(249, 248, 246)
            )
        )

        WindowCompat.getInsetsController(
            window,
            window.decorView
        ).isAppearanceLightNavigationBars = true

        setContent {
            SanaEscenciaTheme {
                AppNavigation(
                    recoveryToken = recoveryToken.value
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        recoveryToken.value = obtenerTokenRecuperacion(intent)
    }

    private fun obtenerTokenRecuperacion(intent: Intent?): String? {
        val uri: Uri = intent?.data ?: return null

        if (intent.action != Intent.ACTION_VIEW ||
            uri.scheme != "https" ||
            uri.host != "sanaesencia-app.duckdns.org" ||
            uri.path != "/reset-password"
        ) {
            return null
        }

        return uri.getQueryParameter("token")
            ?.takeIf { it.isNotBlank() }
    }
}
