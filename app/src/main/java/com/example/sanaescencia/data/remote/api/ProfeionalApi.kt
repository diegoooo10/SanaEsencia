
package com.example.sanaescencia.data.remote.api

import com.example.sanaescencia.data.model.Profesional
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface ProfesionalApiService {

    @GET("api/profesionales")
    suspend fun obtenerProfesionales(
        @Header("Authorization") token: String
    ): Response<List<Profesional>>
}
