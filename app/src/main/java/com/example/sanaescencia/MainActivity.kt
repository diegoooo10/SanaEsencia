
package com.example.sanaescencia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.SystemBarStyle
import androidx.core.view.WindowCompat

import com.example.sanaescencia.navigation.AppNavigation
import com.example.sanaescencia.ui.theme.SanaEscenciaTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                AppNavigation()
            }
        }
    }
}
