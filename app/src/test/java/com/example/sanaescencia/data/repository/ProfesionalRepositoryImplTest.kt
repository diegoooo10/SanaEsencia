package com.example.sanaescencia.data.repository

import com.example.sanaescencia.data.model.Profesional
import com.example.sanaescencia.data.remote.api.ProfesionalApiService
import com.example.sanaescencia.testutil.TestData
import com.example.sanaescencia.testutil.responseHttpError
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import retrofit2.Response
import java.io.IOException

class ProfesionalRepositoryImplTest : FunSpec({

    lateinit var api: ProfesionalApiService
    lateinit var repository: ProfesionalRepositoryImpl

    beforeTest {
        api = mockk()
        repository = ProfesionalRepositoryImpl(api)
    }

    test("devuelve los profesionales y envia el token con prefijo Bearer") {
        runTest {
            coEvery { api.obtenerProfesionales("Bearer abc") } returns
                Response.success(listOf(TestData.profesional))

            val result = repository.obtenerProfesionales("abc")

            result.getOrNull() shouldBe listOf(TestData.profesional)
            coVerify(exactly = 1) { api.obtenerProfesionales("Bearer abc") }
        }
    }

    test("falla cuando el cuerpo de la respuesta es nulo") {
        runTest {
            coEvery { api.obtenerProfesionales(any()) } returns
                Response.success<List<Profesional>>(null)

            val result = repository.obtenerProfesionales("abc")

            result.isFailure shouldBe true
            result.exceptionOrNull()?.message shouldBe
                "El servidor devolvio una respuesta vacia"
        }
    }

    test("falla con el codigo HTTP cuando la respuesta no es exitosa") {
        runTest {
            coEvery { api.obtenerProfesionales(any()) } returns responseHttpError(503)

            val result = repository.obtenerProfesionales("abc")

            result.isFailure shouldBe true
            result.exceptionOrNull()?.message shouldBe "Error HTTP 503"
        }
    }

    test("captura las excepciones de red y las convierte en Result failure") {
        runTest {
            val error = IOException("sin conexion")
            coEvery { api.obtenerProfesionales(any()) } throws error

            val result = repository.obtenerProfesionales("abc")

            result.isFailure shouldBe true
            result.exceptionOrNull() shouldBe error
        }
    }

    test("propaga CancellationException sin envolverla") {
        coEvery { api.obtenerProfesionales(any()) } throws CancellationException("cancelado")

        runBlocking {
            shouldThrow<CancellationException> {
                repository.obtenerProfesionales("abc")
            }
        }
    }
})
