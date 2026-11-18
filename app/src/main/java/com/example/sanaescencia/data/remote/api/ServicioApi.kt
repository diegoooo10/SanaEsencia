
package com.example.sanaescencia.data.remote.api

import com.example.sanaescencia.data.model.Especialidad
import com.example.sanaescencia.data.model.Servicio
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface ServicioApiService {

    @GET("api/especialidades")
    suspend fun obtenerEspecialidades(
        @Header("Authorization") token: String
    ): Response<List<Especialidad>>

    @GET("api/servicios")
    suspend fun obtenerServicios(
        @Header("Authorization") token: String,
        @Query("especialidadId") especialidadId: Int? = null
    ): Response<List<Servicio>>

}
