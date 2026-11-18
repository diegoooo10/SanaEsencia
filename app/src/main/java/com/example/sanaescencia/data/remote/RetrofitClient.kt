package com.example.sanaescencia.data.remote

import com.example.sanaescencia.data.remote.api.UsuarioApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import com.example.sanaescencia.data.remote.api.ProfesionalApiService
import com.example.sanaescencia.data.remote.api.ServicioApiService
import com.example.sanaescencia.data.remote.api.ReservaApiService
import com.google.gson.GsonBuilder
import com.example.sanaescencia.data.remote.adapter.LocalDateAdapter
import com.example.sanaescencia.data.remote.adapter.LocalTimeAdapter
import java.time.LocalDate
import java.time.LocalTime

object RetrofitClient {

    private const val BASE_URL = "https://sanaesencia-app.duckdns.org/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private val gson = GsonBuilder()
        .registerTypeAdapter(
            LocalDate::class.java,
            LocalDateAdapter()
        )
        .registerTypeAdapter(
            LocalTime::class.java,
            LocalTimeAdapter()
        )
        .create()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val usuarioApiService: UsuarioApiService =
        retrofit.create(UsuarioApiService::class.java)

    val profesionalApiService: ProfesionalApiService =
        retrofit.create(ProfesionalApiService::class.java)

    val servicioApiService: ServicioApiService =
        retrofit.create(ServicioApiService::class.java)

    val reservaApiService: ReservaApiService =
        retrofit.create(ReservaApiService::class.java)

}