package com.example.sanaescencia.data.repository

import com.example.sanaescencia.data.remote.api.UsuarioApiService
import com.example.sanaescencia.data.remote.dto.UsuarioResponse
import com.example.sanaescencia.testutil.TestData
import com.example.sanaescencia.testutil.responseHttpError
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

/**
 * Pruebas del repositorio de usuarios con Kotest y MockK.
 *
 * Se usa MockK (y no un fake manual) porque aqui el objetivo es verificar la
 * interaccion exacta con [UsuarioApiService]: que endpoint se invoca, con que
 * token y como se traduce cada respuesta a un [Result].
 */
class UsuarioRepositoryImplTest : FunSpec({

    lateinit var api: UsuarioApiService
    lateinit var repository: UsuarioRepositoryImpl

    beforeTest {
        api = mockk()
        repository = UsuarioRepositoryImpl(api)
    }

    // --- Registro ---

    test("registra un usuario y envia el request completo") {
        runTest {
            coEvery { api.registrar(TestData.usuarioRequest) } returns
                Response.success(TestData.usuarioResponse)

            val result = repository.registrar(TestData.usuarioRequest)

            result.getOrNull() shouldBe TestData.usuarioResponse
            coVerify(exactly = 1) { api.registrar(TestData.usuarioRequest) }
        }
    }

    test("registro con cuerpo nulo devuelve failure descriptivo") {
        runTest {
            coEvery { api.registrar(any()) } returns Response.success<UsuarioResponse>(null)

            val result = repository.registrar(TestData.usuarioRequest)

            result.isFailure shouldBe true
            result.exceptionOrNull()?.message.orEmpty()
                .shouldContain("No pudimos completar tu solicitud")
        }
    }

    test("registro con error HTTP devuelve HttpException") {
        runTest {
            coEvery { api.registrar(any()) } returns responseHttpError(409)

            val result = repository.registrar(TestData.usuarioRequest)

            result.exceptionOrNull()
                .shouldBeInstanceOf<HttpException>()
                .code() shouldBe 409
        }
    }

    test("registro captura excepciones de red") {
        runTest {
            coEvery { api.registrar(any()) } throws IOException("registro")

            val result = repository.registrar(TestData.usuarioRequest)

            result.exceptionOrNull().shouldBeInstanceOf<IOException>()
        }
    }

    // --- Login ---

    test("inicia sesion correctamente") {
        runTest {
            coEvery { api.iniciarSesion(TestData.loginRequest) } returns
                Response.success(TestData.loginResponse)

            val result = repository.iniciarSesion(TestData.loginRequest)

            result.getOrNull() shouldBe TestData.loginResponse
            coVerify(exactly = 1) { api.iniciarSesion(TestData.loginRequest) }
        }
    }

    test("login con cuerpo nulo devuelve failure") {
        runTest {
            coEvery { api.iniciarSesion(any()) } returns
                Response.success<com.example.sanaescencia.data.remote.dto.LoginResponse>(null)

            val result = repository.iniciarSesion(TestData.loginRequest)

            result.isFailure shouldBe true
        }
    }

    test("login con credenciales invalidas devuelve HttpException 401") {
        runTest {
            coEvery { api.iniciarSesion(any()) } returns responseHttpError(401)

            val result = repository.iniciarSesion(TestData.loginRequest)

            result.exceptionOrNull()
                .shouldBeInstanceOf<HttpException>()
                .code() shouldBe 401
        }
    }

    test("login captura excepciones") {
        runTest {
            coEvery { api.iniciarSesion(any()) } throws IOException("login")

            val result = repository.iniciarSesion(TestData.loginRequest)

            result.exceptionOrNull().shouldBeInstanceOf<IOException>()
        }
    }

    // --- Perfil ---

    test("obtiene el perfil con el token Bearer") {
        runTest {
            coEvery { api.obtenerPerfil("Bearer tk") } returns
                Response.success(TestData.usuarioResponse)

            val result = repository.obtenerPerfil("tk")

            result.getOrNull() shouldBe TestData.usuarioResponse
            coVerify(exactly = 1) { api.obtenerPerfil("Bearer tk") }
        }
    }

    test("perfil con cuerpo nulo devuelve failure") {
        runTest {
            coEvery { api.obtenerPerfil(any()) } returns Response.success<UsuarioResponse>(null)

            val result = repository.obtenerPerfil("tk")

            result.isFailure shouldBe true
        }
    }

    test("perfil con error HTTP devuelve HttpException") {
        runTest {
            coEvery { api.obtenerPerfil(any()) } returns responseHttpError(403)

            val result = repository.obtenerPerfil("tk")

            result.exceptionOrNull()
                .shouldBeInstanceOf<HttpException>()
                .code() shouldBe 403
        }
    }

    test("perfil captura excepciones") {
        runTest {
            coEvery { api.obtenerPerfil(any()) } throws IOException("perfil")

            val result = repository.obtenerPerfil("tk")

            result.exceptionOrNull().shouldBeInstanceOf<IOException>()
        }
    }

    // --- Actualizacion de perfil ---

    test("actualiza el perfil con token y request") {
        runTest {
            coEvery {
                api.actualizarPerfil("Bearer tk", TestData.usuarioUpdateRequest)
            } returns Response.success(TestData.usuarioResponse)

            val result = repository.actualizarPerfil("tk", TestData.usuarioUpdateRequest)

            result.getOrNull() shouldBe TestData.usuarioResponse
            coVerify(exactly = 1) {
                api.actualizarPerfil("Bearer tk", TestData.usuarioUpdateRequest)
            }
        }
    }

    test("actualizacion con cuerpo nulo devuelve failure") {
        runTest {
            coEvery { api.actualizarPerfil(any(), any()) } returns
                Response.success<UsuarioResponse>(null)

            val result = repository.actualizarPerfil("tk", TestData.usuarioUpdateRequest)

            result.isFailure shouldBe true
        }
    }

    test("actualizacion con error HTTP devuelve HttpException") {
        runTest {
            coEvery { api.actualizarPerfil(any(), any()) } returns responseHttpError(422)

            val result = repository.actualizarPerfil("tk", TestData.usuarioUpdateRequest)

            result.exceptionOrNull()
                .shouldBeInstanceOf<HttpException>()
                .code() shouldBe 422
        }
    }

    test("actualizacion captura excepciones") {
        runTest {
            coEvery { api.actualizarPerfil(any(), any()) } throws IOException("actualizar")

            val result = repository.actualizarPerfil("tk", TestData.usuarioUpdateRequest)

            result.exceptionOrNull().shouldBeInstanceOf<IOException>()
        }
    }

    // --- Recuperacion ---

    test("solicita recuperacion de password") {
        runTest {
            coEvery { api.solicitarRecuperacion(TestData.recuperacionRequest) } returns
                Response.success<ResponseBody>(null)

            val result = repository.solicitarRecuperacion(TestData.recuperacionRequest)

            result.getOrNull() shouldBe Unit
            coVerify(exactly = 1) {
                api.solicitarRecuperacion(TestData.recuperacionRequest)
            }
        }
    }

    test("recuperacion con error HTTP devuelve failure") {
        runTest {
            coEvery { api.solicitarRecuperacion(any()) } returns responseHttpError(429)

            val result = repository.solicitarRecuperacion(TestData.recuperacionRequest)

            result.exceptionOrNull()?.message shouldBe "Error HTTP 429"
        }
    }

    test("recuperacion captura excepciones") {
        runTest {
            coEvery { api.solicitarRecuperacion(any()) } throws IOException("recuperacion")

            val result = repository.solicitarRecuperacion(TestData.recuperacionRequest)

            result.exceptionOrNull().shouldBeInstanceOf<IOException>()
        }
    }

    // --- Restablecimiento ---

    test("restablece la password") {
        runTest {
            coEvery { api.restablecerPassword(TestData.restablecerRequest) } returns
                Response.success<ResponseBody>(null)

            val result = repository.restablecerPassword(TestData.restablecerRequest)

            result.getOrNull() shouldBe Unit
            coVerify(exactly = 1) {
                api.restablecerPassword(TestData.restablecerRequest)
            }
        }
    }

    test("restablecimiento con error HTTP devuelve failure") {
        runTest {
            coEvery { api.restablecerPassword(any()) } returns responseHttpError(410)

            val result = repository.restablecerPassword(TestData.restablecerRequest)

            result.exceptionOrNull()?.message shouldBe "Error HTTP 410"
        }
    }

    test("restablecimiento captura excepciones") {
        runTest {
            coEvery { api.restablecerPassword(any()) } throws IOException("restablecer")

            val result = repository.restablecerPassword(TestData.restablecerRequest)

            result.exceptionOrNull().shouldBeInstanceOf<IOException>()
        }
    }

    // --- Cancelacion ---

    test("propaga CancellationException en todos los endpoints") {
        coEvery { api.registrar(any()) } throws CancellationException("cancelado")
        coEvery { api.iniciarSesion(any()) } throws CancellationException("cancelado")
        coEvery { api.obtenerPerfil(any()) } throws CancellationException("cancelado")
        coEvery { api.actualizarPerfil(any(), any()) } throws CancellationException("cancelado")
        coEvery { api.solicitarRecuperacion(any()) } throws CancellationException("cancelado")
        coEvery { api.restablecerPassword(any()) } throws CancellationException("cancelado")

        runBlocking {
            shouldThrow<CancellationException> {
                repository.registrar(TestData.usuarioRequest)
            }
            shouldThrow<CancellationException> {
                repository.iniciarSesion(TestData.loginRequest)
            }
            shouldThrow<CancellationException> {
                repository.obtenerPerfil("tk")
            }
            shouldThrow<CancellationException> {
                repository.actualizarPerfil("tk", TestData.usuarioUpdateRequest)
            }
            shouldThrow<CancellationException> {
                repository.solicitarRecuperacion(TestData.recuperacionRequest)
            }
            shouldThrow<CancellationException> {
                repository.restablecerPassword(TestData.restablecerRequest)
            }
        }
    }
})
