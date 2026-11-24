package com.example.sanaescencia.data.remote.adapter

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.LocalTime
import java.time.format.DateTimeParseException

class LocalTimeAdapterTest : FunSpec({

    val gson: Gson = GsonBuilder()
        .registerTypeAdapter(LocalTime::class.java, LocalTimeAdapter())
        .create()

    test("serializa una hora en formato ISO") {
        gson.toJson(LocalTime.of(9, 30)) shouldBe "\"09:30\""
    }

    test("deserializa una hora en formato ISO") {
        gson.fromJson("\"14:45:00\"", LocalTime::class.java) shouldBe LocalTime.of(14, 45)
    }

    test("round trip conserva la hora") {
        val original = LocalTime.of(23, 59, 59)

        val json = gson.toJson(original)
        val recuperada = gson.fromJson(json, LocalTime::class.java)

        recuperada shouldBe original
    }

    test("write con valor nulo produce null") {
        val gsonConNulos = GsonBuilder()
            .registerTypeAdapter(LocalTime::class.java, LocalTimeAdapter())
            .serializeNulls()
            .create()

        gsonConNulos.toJson(null, LocalTime::class.java) shouldBe "null"
    }

    test("read de null devuelve null") {
        gson.fromJson("null", LocalTime::class.java) shouldBe null
    }

    test("una hora invalida lanza excepcion de parseo") {
        shouldThrow<DateTimeParseException> {
            gson.fromJson("\"25:99\"", LocalTime::class.java)
        }
    }
})
