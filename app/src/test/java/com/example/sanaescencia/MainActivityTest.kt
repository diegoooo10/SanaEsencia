package com.example.sanaescencia

import android.content.Intent
import android.net.Uri
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MainActivityTest {

    private val activity: MainActivity =
        Robolectric.buildActivity(MainActivity::class.java).get()

    private val metodo = MainActivity::class.java
        .getDeclaredMethod("obtenerTokenRecuperacion", Intent::class.java)
        .apply { isAccessible = true }

    private fun token(intent: Intent?): String? =
        metodo.invoke(activity, intent) as String?

    private fun intent(
        action: String = Intent.ACTION_VIEW,
        url: String = "https://sanaesencia-app.duckdns.org/reset-password?token=abc123"
    ): Intent = Intent(action, Uri.parse(url))

    @Test
    fun `intent nulo no entrega token`() {
        assertNull(token(null))
    }

    @Test
    fun `intent sin data no entrega token`() {
        assertNull(token(Intent(Intent.ACTION_VIEW)))
    }

    @Test
    fun `accion distinta de VIEW no entrega token`() {
        assertNull(token(intent(action = Intent.ACTION_SEND)))
    }

    @Test
    fun `esquema http no entrega token`() {
        assertNull(
            token(
                intent(
                    url = "http://sanaesencia-app.duckdns.org/reset-password?token=abc123"
                )
            )
        )
    }

    @Test
    fun `host incorrecto no entrega token`() {
        assertNull(
            token(
                intent(
                    url = "https://otro-dominio.cl/reset-password?token=abc123"
                )
            )
        )
    }

    @Test
    fun `path incorrecto no entrega token`() {
        assertNull(
            token(
                intent(
                    url = "https://sanaesencia-app.duckdns.org/otra-ruta?token=abc123"
                )
            )
        )
    }

    @Test
    fun `sin parametro token no entrega token`() {
        assertNull(
            token(
                intent(
                    url = "https://sanaesencia-app.duckdns.org/reset-password"
                )
            )
        )
    }

    @Test
    fun `token en blanco no entrega token`() {
        assertNull(
            token(
                intent(
                    url = "https://sanaesencia-app.duckdns.org/reset-password?token=%20"
                )
            )
        )
    }

    @Test
    fun `app link valido entrega el token`() {
        assertEquals(
            "abc123",
            token(
                intent(
                    url = "https://sanaesencia-app.duckdns.org/reset-password?token=abc123"
                )
            )
        )
    }

    @Test
    fun `onCreate procesa el app link inicial`() {
        val controller = Robolectric.buildActivity(
            MainActivity::class.java,
            intent()
        ).setup()

        assertEquals("abc123", controller.get().intent.data?.getQueryParameter("token"))
        controller.pause().stop().destroy()
    }

    @Test
    fun `onNewIntent actualiza el intent recibido`() {
        val controller = Robolectric.buildActivity(MainActivity::class.java).setup()
        val activity = controller.get()

        controller.newIntent(
            intent(
                url = "https://sanaesencia-app.duckdns.org/reset-password?token=nuevo-token"
            )
        )
        assertEquals(
            "nuevo-token",
            activity.intent.data?.getQueryParameter("token")
        )
        controller.pause().stop().destroy()
    }
}
