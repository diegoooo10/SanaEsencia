package com.example.sanaescencia.data.remote

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClientTest : FunSpec({

    test("expone la url base del backend") {
        RetrofitClient.retrofit.baseUrl().toString() shouldBe
            "https://sanaesencia-app.duckdns.org/"
    }

    test("crea los cuatro servicios de la API") {
        RetrofitClient.usuarioApiService shouldNotBe null
        RetrofitClient.profesionalApiService shouldNotBe null
        RetrofitClient.servicioApiService shouldNotBe null
        RetrofitClient.reservaApiService shouldNotBe null
    }

    test("los servicios usan el converter Gson") {
        RetrofitClient.retrofit.converterFactories()
            .any { it is GsonConverterFactory } shouldBe true
    }
})
