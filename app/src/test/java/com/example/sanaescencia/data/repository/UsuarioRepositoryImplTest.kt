package com.example.sanaescencia.data.repository

import com.example.sanaescencia.testutil.FakeUsuarioApi
import com.example.sanaescencia.testutil.TestData
import com.example.sanaescencia.testutil.responseHttpError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class UsuarioRepositoryImplTest {

    private val api = FakeUsuarioApi()
    private val repository = UsuarioRepositoryImpl(api)

    // --- Registro ---

    @Test
    fun `registra un usuario y envia el request completo`() = runTest {
        api.registrarResponse = Response.success(TestData.usuarioResponse)

        val result = repository.registrar(TestData.usuarioRequest)

        assertEquals(TestData.usuarioResponse, result.getOrNull())
        assertEquals(TestData.usuarioRequest, api.lastRegistrarRequest)
    }

    @Test
    fun `registro con cuerpo nulo devuelve failure descriptivo`() = runTest {
        api.registrarResponse = Response.success(null)

        val result = repository.registrar(TestData.usuarioRequest)

        assertTrue(result.isFailure)
        assertTrue(
            result.exceptionOrNull()?.message
                .orEmpty()
                .contains("No pudimos completar tu solicitud")
        )
    }

    @Test
    fun `registro con error HTTP devuelve HttpException`() = runTest {
        api.registrarResponse = responseHttpError(409)

        val result = repository.registrar(TestData.usuarioRequest)

        val error = result.exceptionOrNull()
        assertTrue(error is HttpException)
        assertEquals(409, (error as HttpException).code())
    }

    @Test
    fun `registro captura excepciones de red`() = runTest {
        api.error = IOException("registro")

        val result = repository.registrar(TestData.usuarioRequest)

        assertTrue(result.exceptionOrNull() is IOException)
    }

    // --- Login ---

    @Test
    fun `inicia sesion correctamente`() = runTest {
        api.loginResponse = Response.success(TestData.loginResponse)

        val result = repository.iniciarSesion(TestData.loginRequest)

        assertEquals(TestData.loginResponse, result.getOrNull())
        assertEquals(TestData.loginRequest, api.lastLoginRequest)
    }

    @Test
    fun `login con cuerpo nulo devuelve failure`() = runTest {
        api.loginResponse = Response.success(null)

        val result = repository.iniciarSesion(TestData.loginRequest)

        assertTrue(result.isFailure)
    }

    @Test
    fun `login con credenciales invalidas devuelve HttpException 401`() = runTest {
        api.loginResponse = responseHttpError(401)

        val result = repository.iniciarSesion(TestData.loginRequest)

        val error = result.exceptionOrNull()
        assertTrue(error is HttpException)
        assertEquals(401, (error as HttpException).code())
    }

    @Test
    fun `login captura excepciones`() = runTest {
        api.error = IOException("login")

        val result = repository.iniciarSesion(TestData.loginRequest)

        assertTrue(result.exceptionOrNull() is IOException)
    }

    // --- Perfil ---

    @Test
    fun `obtiene el perfil con el token Bearer`() = runTest {
        api.perfilResponse = Response.success(TestData.usuarioResponse)

        val result = repository.obtenerPerfil("tk")

        assertEquals(TestData.usuarioResponse, result.getOrNull())
        assertEquals("Bearer tk", api.lastPerfilToken)
    }

    @Test
    fun `perfil con cuerpo nulo devuelve failure`() = runTest {
        api.perfilResponse = Response.success(null)

        val result = repository.obtenerPerfil("tk")

        assertTrue(result.isFailure)
    }

    @Test
    fun `perfil con error HTTP devuelve HttpException`() = runTest {
        api.perfilResponse = responseHttpError(403)

        val result = repository.obtenerPerfil("tk")

        assertEquals(403, (result.exceptionOrNull() as HttpException).code())
    }

    @Test
    fun `perfil captura excepciones`() = runTest {
        api.error = IOException("perfil")

        val result = repository.obtenerPerfil("tk")

        assertTrue(result.exceptionOrNull() is IOException)
    }

    // --- Actualizacion de perfil ---

    @Test
    fun `actualiza el perfil con token y request`() = runTest {
        api.actualizarResponse = Response.success(TestData.usuarioResponse)

        val result = repository.actualizarPerfil("tk", TestData.usuarioUpdateRequest)

        assertEquals(TestData.usuarioResponse, result.getOrNull())
        assertEquals("Bearer tk", api.lastActualizarToken)
        assertEquals(TestData.usuarioUpdateRequest, api.lastActualizarRequest)
    }

    @Test
    fun `actualizacion con cuerpo nulo devuelve failure`() = runTest {
        api.actualizarResponse = Response.success(null)

        val result = repository.actualizarPerfil("tk", TestData.usuarioUpdateRequest)

        assertTrue(result.isFailure)
    }

    @Test
    fun `actualizacion con error HTTP devuelve HttpException`() = runTest {
        api.actualizarResponse = responseHttpError(422)

        val result = repository.actualizarPerfil("tk", TestData.usuarioUpdateRequest)

        assertEquals(422, (result.exceptionOrNull() as HttpException).code())
    }

    @Test
    fun `actualizacion captura excepciones`() = runTest {
        api.error = IOException("actualizar")

        val result = repository.actualizarPerfil("tk", TestData.usuarioUpdateRequest)

        assertTrue(result.exceptionOrNull() is IOException)
    }

    // --- Recuperacion ---

    @Test
    fun `solicita recuperacion de password`() = runTest {
        val result = repository.solicitarRecuperacion(TestData.recuperacionRequest)

        assertEquals(Unit, result.getOrNull())
        assertEquals(TestData.recuperacionRequest, api.lastRecuperacionRequest)
    }

    @Test
    fun `recuperacion con error HTTP devuelve failure`() = runTest {
        api.recuperacionResponse = responseHttpError(429)

        val result = repository.solicitarRecuperacion(TestData.recuperacionRequest)

        assertEquals("Error HTTP 429", result.exceptionOrNull()?.message)
    }

    @Test
    fun `recuperacion captura excepciones`() = runTest {
        api.error = IOException("recuperacion")

        val result = repository.solicitarRecuperacion(TestData.recuperacionRequest)

        assertTrue(result.exceptionOrNull() is IOException)
    }

    // --- Restablecimiento ---

    @Test
    fun `restablece la password`() = runTest {
        val result = repository.restablecerPassword(TestData.restablecerRequest)

        assertEquals(Unit, result.getOrNull())
        assertEquals(TestData.restablecerRequest, api.lastRestablecerRequest)
    }

    @Test
    fun `restablecimiento con error HTTP devuelve failure`() = runTest {
        api.restablecerResponse = responseHttpError(410)

        val result = repository.restablecerPassword(TestData.restablecerRequest)

        assertEquals("Error HTTP 410", result.exceptionOrNull()?.message)
    }

    @Test
    fun `restablecimiento captura excepciones`() = runTest {
        api.error = IOException("restablecer")

        val result = repository.restablecerPassword(TestData.restablecerRequest)

        assertTrue(result.exceptionOrNull() is IOException)
    }

    // --- Cancelacion ---

    @Test
    fun `propaga CancellationException en todos los endpoints`() {
        api.error = CancellationException("cancelado")

        assertThrows(CancellationException::class.java) {
            runBlocking { repository.registrar(TestData.usuarioRequest) }
        }
        assertThrows(CancellationException::class.java) {
            runBlocking { repository.iniciarSesion(TestData.loginRequest) }
        }
        assertThrows(CancellationException::class.java) {
            runBlocking { repository.obtenerPerfil("tk") }
        }
        assertThrows(CancellationException::class.java) {
            runBlocking { repository.actualizarPerfil("tk", TestData.usuarioUpdateRequest) }
        }
        assertThrows(CancellationException::class.java) {
            runBlocking { repository.solicitarRecuperacion(TestData.recuperacionRequest) }
        }
        assertThrows(CancellationException::class.java) {
            runBlocking { repository.restablecerPassword(TestData.restablecerRequest) }
        }
    }
}
