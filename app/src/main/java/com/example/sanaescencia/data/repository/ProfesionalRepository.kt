
package com.example.sanaescencia.data.repository

import com.example.sanaescencia.data.model.Profesional

interface ProfesionalRepository {

    suspend fun obtenerProfesionales(
        token: String
    ): Result<List<Profesional>>

}
