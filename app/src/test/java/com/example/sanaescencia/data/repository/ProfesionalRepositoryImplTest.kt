package com.example.sanaescencia.data.repository

import com.example.sanaescencia.testutil.FakeProfesionalApi
import com.example.sanaescencia.testutil.TestData
import com.example.sanaescencia.testutil.responseHttpError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response
import java.io.IOException

class ProfesionalRepositoryImplTest {

    private val api = FakeProfesionalApi()
    private val repository = ProfesionalRepositoryImpl(api)

    @Test
    fun `devuelve los profesionales y envia el token con prefijo Bearer`() = runTest {
        api.response = Response.success(listOf(TestData.profesional))

        val result = repository.obtenerProfesionales("abc")

        assertEquals(listOf(TestData.profesional), result.getOrNull())
        assertEquals("Bearer abc", api.lastToken)
    }

    @Test
    fun `falla cuando el cuerpo de la respuesta es nulo`() = runTest {
        api.response = Response.success(null)

        val result = repository.obtenerProfesionales("abc")

        assertTrue(result.isFailure)
        assertEquals(
            "El servidor devolvio una respuesta vacia",
            result.exceptionOrNull()?.message
        )
    }

    @Test
    fun `falla con el codigo HTTP cuando la respuesta no es exitosa`() = runTest {
        api.response = responseHttpError(503)

        val result = repository.obtenerProfesionales("abc")

        assertTrue(result.isFailure)
        assertEquals("Error HTTP 503", result.exceptionOrNull()?.message)
    }

    @Test
    fun `captura las excepciones de red y las convierte en Result failure`() = runTest {
        val error = IOException("sin conexion")
        api.error = error

        val result = repository.obtenerProfesionales("abc")

        assertTrue(result.isFailure)
        assertEquals(error, result.exceptionOrNull())
    }

    @Test
    fun `propaga CancellationException sin envolverla`() {
        api.error = CancellationException("cancelado")

        assertThrows(CancellationException::class.java) {
            runBlocking { repository.obtenerProfesionales("abc") }
        }
    }
}
