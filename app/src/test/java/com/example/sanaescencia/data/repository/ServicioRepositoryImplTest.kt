package com.example.sanaescencia.data.repository

import com.example.sanaescencia.data.model.Especialidad
import com.example.sanaescencia.data.model.Servicio
import com.example.sanaescencia.data.remote.api.ServicioApiService
import com.example.sanaescencia.testutil.TestData
import com.example.sanaescencia.testutil.responseHttpError
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import retrofit2.Response
import java.io.IOException

class ServicioRepositoryImplTest : FunSpec({

    lateinit var api: ServicioApiService
    lateinit var repository: ServicioRepositoryImpl

    beforeTest {
        api = mockk()
        repository = ServicioRepositoryImpl(api)
    }

    test("obtiene especialidades enviando el token Bearer") {
        runTest {
            coEvery { api.obtenerEspecialidades("Bearer token-1") } returns
                Response.success(listOf(TestData.especialidad))

            val result = repository.obtenerEspecialidades("token-1")

            result.getOrNull() shouldBe listOf(TestData.especialidad)
            coVerify(exactly = 1) { api.obtenerEspecialidades("Bearer token-1") }
        }
    }

    test("especialidades con cuerpo nulo devuelve failure") {
        runTest {
            coEvery { api.obtenerEspecialidades(any()) } returns
                Response.success<List<Especialidad>>(null)

            val result = repository.obtenerEspecialidades("token-1")

            result.isFailure shouldBe true
            result.exceptionOrNull()?.message shouldBe
                "El servidor devolvio una respuesta vacia"
        }
    }

    test("especialidades con error HTTP devuelve failure") {
        runTest {
            coEvery { api.obtenerEspecialidades(any()) } returns responseHttpError(500)

            val result = repository.obtenerEspecialidades("token-1")

            result.exceptionOrNull()?.message shouldBe "Error HTTP 500"
        }
    }

    test("especialidades captura excepciones") {
        runTest {
            coEvery { api.obtenerEspecialidades(any()) } throws IOException("timeout")

            val result = repository.obtenerEspecialidades("token-1")

            result.exceptionOrNull().shouldBeInstanceOf<IOException>()
        }
    }

    test("obtiene servicios sin filtro de especialidad") {
        runTest {
            coEvery { api.obtenerServicios("Bearer token-2", null) } returns
                Response.success(listOf(TestData.servicio))

            val result = repository.obtenerServicios("token-2", null)

            result.getOrNull() shouldBe listOf(TestData.servicio)
            coVerify(exactly = 1) { api.obtenerServicios("Bearer token-2", null) }
        }
    }

    test("obtiene servicios filtrando por especialidad") {
        runTest {
            coEvery { api.obtenerServicios("Bearer token-2", 7) } returns
                Response.success(listOf(TestData.servicio))

            val result = repository.obtenerServicios("token-2", 7)

            result.isSuccess shouldBe true
            coVerify(exactly = 1) { api.obtenerServicios("Bearer token-2", 7) }
        }
    }

    test("servicios con cuerpo nulo devuelve failure") {
        runTest {
            coEvery { api.obtenerServicios(any(), any()) } returns
                Response.success<List<Servicio>>(null)

            val result = repository.obtenerServicios("token-2", 1)

            result.exceptionOrNull()?.message shouldBe
                "El servidor devolvio una respuesta vacia"
        }
    }

    test("servicios con error HTTP devuelve failure") {
        runTest {
            coEvery { api.obtenerServicios(any(), any()) } returns responseHttpError(404)

            val result = repository.obtenerServicios("token-2", 1)

            result.exceptionOrNull()?.message shouldBe "Error HTTP 404"
        }
    }

    test("servicios captura excepciones") {
        runTest {
            coEvery { api.obtenerServicios(any(), any()) } throws
                IllegalStateException("boom")
            val result = repository.obtenerServicios("token-2", 1)

            result.exceptionOrNull()?.message shouldBe "boom"
        }
    }

    test("propaga CancellationException en especialidades") {
        coEvery { api.obtenerEspecialidades(any()) } throws CancellationException("cancelado")

        runBlocking {
            shouldThrow<CancellationException> {
                repository.obtenerEspecialidades("t")
            }
        }
    }

    test("propaga CancellationException en servicios") {
        coEvery { api.obtenerServicios(any(), any()) } throws CancellationException("cancelado")

        runBlocking {
            shouldThrow<CancellationException> {
                repository.obtenerServicios("t", null)
            }
        }
    }
})
