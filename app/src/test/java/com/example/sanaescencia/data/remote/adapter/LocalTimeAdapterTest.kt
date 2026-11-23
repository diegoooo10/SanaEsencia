package com.example.sanaescencia.data.remote.adapter

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test
import java.time.LocalTime

class LocalTimeAdapterTest {

    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(LocalTime::class.java, LocalTimeAdapter())
        .create()

    @Test
    fun `serializa una hora en formato ISO`() {
        assertEquals("\"09:30\"", gson.toJson(LocalTime.of(9, 30)))
    }

    @Test
    fun `deserializa una hora en formato ISO`() {
        assertEquals(
            LocalTime.of(14, 45),
            gson.fromJson("\"14:45:00\"", LocalTime::class.java)
        )
    }

    @Test
    fun `round trip conserva la hora`() {
        val original = LocalTime.of(23, 59, 59)

        val json = gson.toJson(original)
        val recuperada = gson.fromJson(json, LocalTime::class.java)

        assertEquals(original, recuperada)
    }

    @Test
    fun `write con valor nulo produce null`() {
        val gsonConNulos = GsonBuilder()
            .registerTypeAdapter(LocalTime::class.java, LocalTimeAdapter())
            .serializeNulls()
            .create()

        assertEquals("null", gsonConNulos.toJson(null, LocalTime::class.java))
    }

    @Test
    fun `read de null devuelve null`() {
        assertNull(gson.fromJson("null", LocalTime::class.java))
    }

    @Test
    fun `una hora invalida lanza excepcion de parseo`() {
        assertThrows(java.time.format.DateTimeParseException::class.java) {
            gson.fromJson("\"25:99\"", LocalTime::class.java)
        }
    }
}
