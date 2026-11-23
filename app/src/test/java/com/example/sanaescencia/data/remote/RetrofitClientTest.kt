package com.example.sanaescencia.data.remote

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RetrofitClientTest {

    @Test
    fun `expone la url base del backend`() {
        assertEquals(
            "https://sanaesencia-app.duckdns.org/",
            RetrofitClient.retrofit.baseUrl().toString()
        )
    }

    @Test
    fun `crea los cuatro servicios de la API`() {
        assertNotNull(RetrofitClient.usuarioApiService)
        assertNotNull(RetrofitClient.profesionalApiService)
        assertNotNull(RetrofitClient.servicioApiService)
        assertNotNull(RetrofitClient.reservaApiService)
    }

    @Test
    fun `los servicios usan el converter Gson`() {
        val factories = RetrofitClient.retrofit.converterFactories()

        assertTrue(
            factories.any { it is retrofit2.converter.gson.GsonConverterFactory }
        )
    }
}
