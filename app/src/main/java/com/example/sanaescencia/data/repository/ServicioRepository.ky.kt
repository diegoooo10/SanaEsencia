
package com.example.sanaescencia.data.repository

import com.example.sanaescencia.data.model.Especialidad
import com.example.sanaescencia.data.model.Servicio

interface ServicioRepository {

    suspend fun obtenerEspecialidades(
        token: String
    ): Result<List<Especialidad>>

    suspend fun obtenerServicios(
        token: String,
        especialidadId: Int? = null
    ): Result<List<Servicio>>

}
