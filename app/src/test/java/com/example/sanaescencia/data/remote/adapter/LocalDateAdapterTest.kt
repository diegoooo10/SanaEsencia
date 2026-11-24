package com.example.sanaescencia.data.remote.adapter

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import java.time.LocalDate
import java.time.format.DateTimeParseException

class LocalDateAdapterTest : FunSpec({

    val gson: Gson = GsonBuilder()
        .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
        .create()

    test("serializa una fecha en formato ISO") {
        val fecha = LocalDate.of(2024, 3, 15)

        gson.toJson(fecha) shouldBe "\"2024-03-15\""
    }

    test("deserializa una fecha en formato ISO") {
        val fecha = gson.fromJson("\"2024-03-15\"", LocalDate::class.java)

        fecha shouldBe LocalDate.of(2024, 3, 15)
    }

    test("round trip conserva la fecha") {
        val original = LocalDate.of(1999, 12, 31)

        val json = gson.toJson(original)
        val recuperada = gson.fromJson(json, LocalDate::class.java)

        recuperada shouldBe original
    }

    test("write con valor nulo produce null") {
        val gsonConNulos = GsonBuilder()
            .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
            .serializeNulls()
            .create()

        gsonConNulos.toJson(null, LocalDate::class.java) shouldBe "null"
    }

    test("read de null devuelve null") {
        gson.fromJson("null", LocalDate::class.java) shouldBe null
    }

    test("una fecha invalida lanza excepcion de parseo") {
        shouldThrow<DateTimeParseException> {
            gson.fromJson("\"2024-13-40\"", LocalDate::class.java)
        }
    }

    test("round trip para fechas arbitrarias") {
        checkAll(Arb.int(1900..2100), Arb.int(1..12), Arb.int(1..28)) { anio, mes, dia ->
            val fecha = LocalDate.of(anio, mes, dia)

            gson.fromJson(gson.toJson(fecha), LocalDate::class.java) shouldBe fecha
        }
    }
})
