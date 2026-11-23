package com.example.sanaescencia.data.repository

import com.example.sanaescencia.testutil.FakeServicioApi
import com.example.sanaescencia.testutil.TestData
import com.example.sanaescencia.testutil.responseHttpError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response
import java.io.IOException

class ServicioRepositoryImplTest {

    private val api = FakeServicioApi()
    private val repository = ServicioRepositoryImpl(api)

    @Test
    fun `obtiene especialidades enviando el token Bearer`() = runTest {
        api.especialidadesResponse = Response.success(listOf(TestData.especialidad))

        val result = repository.obtenerEspecialidades("token-1")

        assertEquals(listOf(TestData.especialidad), result.getOrNull())
        assertEquals("Bearer token-1", api.lastEspecialidadesToken)
    }

    @Test
    fun `especialidades con cuerpo nulo devuelve failure`() = runTest {
        api.especialidadesResponse = Response.success(null)

        val result = repository.obtenerEspecialidades("token-1")

        assertTrue(result.isFailure)
        assertEquals(
            "El servidor devolvio una respuesta vacia",
            result.exceptionOrNull()?.message
        )
    }

    @Test
    fun `especialidades con error HTTP devuelve failure`() = runTest {
        api.especialidadesResponse = responseHttpError(500)

        val result = repository.obtenerEspecialidades("token-1")

        assertEquals("Error HTTP 500", result.exceptionOrNull()?.message)
    }

    @Test
    fun `especialidades captura excepciones`() = runTest {
        api.error = IOException("timeout")

        val result = repository.obtenerEspecialidades("token-1")

        assertTrue(result.exceptionOrNull() is IOException)
    }

    @Test
    fun `obtiene servicios sin filtro de especialidad`() = runTest {
        api.serviciosResponse = Response.success(listOf(TestData.servicio))

        val result = repository.obtenerServicios("token-2", null)

        assertEquals(listOf(TestData.servicio), result.getOrNull())
        assertEquals("Bearer token-2", api.lastServiciosToken)
        assertNull(api.lastEspecialidadId)
    }

    @Test
    fun `obtiene servicios filtrando por especialidad`() = runTest {
        val result = repository.obtenerServicios("token-2", 7)

        assertTrue(result.isSuccess)
        assertEquals(7, api.lastEspecialidadId)
    }

    @Test
    fun `servicios con cuerpo nulo devuelve failure`() = runTest {
        api.serviciosResponse = Response.success(null)

        val result = repository.obtenerServicios("token-2", 1)

        assertEquals(
            "El servidor devolvio una respuesta vacia",
            result.exceptionOrNull()?.message
        )
    }

    @Test
    fun `servicios con error HTTP devuelve failure`() = runTest {
        api.serviciosResponse = responseHttpError(404)

        val result = repository.obtenerServicios("token-2", 1)

        assertEquals("Error HTTP 404", result.exceptionOrNull()?.message)
    }

    @Test
    fun `servicios captura excepciones`() = runTest {
        api.error = IllegalStateException("boom")

        val result = repository.obtenerServicios("token-2", 1)

        assertEquals("boom", result.exceptionOrNull()?.message)
    }

    @Test
    fun `propaga CancellationException en especialidades`() {
        api.error = CancellationException("cancelado")

        assertThrows(CancellationException::class.java) {
            runBlocking { repository.obtenerEspecialidades("t") }
        }
    }

    @Test
    fun `propaga CancellationException en servicios`() {
        api.error = CancellationException("cancelado")

        assertThrows(CancellationException::class.java) {
            runBlocking { repository.obtenerServicios("t", null) }
        }
    }
}
