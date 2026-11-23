package com.example.sanaescencia.data.remote.adapter

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test
import java.time.LocalDate

class LocalDateAdapterTest {

    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
        .create()

    @Test
    fun `serializa una fecha en formato ISO`() {
        val fecha = LocalDate.of(2024, 3, 15)

        assertEquals("\"2024-03-15\"", gson.toJson(fecha))
    }

    @Test
    fun `deserializa una fecha en formato ISO`() {
        val fecha = gson.fromJson("\"2024-03-15\"", LocalDate::class.java)

        assertEquals(LocalDate.of(2024, 3, 15), fecha)
    }

    @Test
    fun `round trip conserva la fecha`() {
        val original = LocalDate.of(1999, 12, 31)

        val json = gson.toJson(original)
        val recuperada = gson.fromJson(json, LocalDate::class.java)

        assertEquals(original, recuperada)
    }

    @Test
    fun `write con valor nulo produce null`() {
        val gsonConNulos = GsonBuilder()
            .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
            .serializeNulls()
            .create()

        assertEquals("null", gsonConNulos.toJson(null, LocalDate::class.java))
    }

    @Test
    fun `read de null devuelve null`() {
        assertNull(gson.fromJson("null", LocalDate::class.java))
    }

    @Test
    fun `una fecha invalida lanza excepcion de parseo`() {
        assertThrows(java.time.format.DateTimeParseException::class.java) {
            gson.fromJson("\"2024-13-40\"", LocalDate::class.java)
        }
    }
}
