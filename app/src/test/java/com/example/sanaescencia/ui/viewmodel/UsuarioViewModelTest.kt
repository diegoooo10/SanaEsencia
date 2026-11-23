package com.example.sanaescencia.ui.viewmodel

import com.example.sanaescencia.testutil.FakeUsuarioRepository
import com.example.sanaescencia.testutil.MainDispatcherRule
import com.example.sanaescencia.testutil.TestData
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class UsuarioViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeUsuarioRepository()
    private val viewModel = UsuarioViewModel(repository)

    @Test
    fun `estados iniciales vacios`() {
        assertNull(viewModel.loginState.value)
        assertNull(viewModel.token.value)
        assertNull(viewModel.perfilState.value)
        assertNull(viewModel.actualizacionState.value)
        assertNull(viewModel.registroState.value)
        assertNull(viewModel.recuperacionState.value)
        assertNull(viewModel.restablecimientoState.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `inicia sesion, guarda el token y carga el perfil`() {
        repository.iniciarSesionResult = Result.success(TestData.loginResponse)
        repository.perfilResult = Result.success(TestData.usuarioResponse)

        viewModel.iniciarSesion("  ana@test.cl  ", "password123")

        assertEquals(TestData.loginResponse, viewModel.loginState.value?.getOrNull())
        assertEquals("jwt-token", viewModel.token.value)
        assertEquals(TestData.usuarioResponse, viewModel.perfilState.value?.getOrNull())
        assertEquals("ana@test.cl", repository.lastLoginRequest?.correo)
        assertEquals("password123", repository.lastLoginRequest?.password)
        assertEquals("jwt-token", repository.lastPerfilToken)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `login fallido no guarda token ni consulta perfil`() {
        val error = IllegalStateException("credenciales")
        repository.iniciarSesionResult = Result.failure(error)

        viewModel.iniciarSesion("ana@test.cl", "mala")

        assertEquals(error, viewModel.loginState.value?.exceptionOrNull())
        assertNull(viewModel.token.value)
        assertEquals(0, repository.perfilCalls)
    }

    @Test
    fun `login captura excepciones`() {
        repository.throwable = IOException("red")

        viewModel.iniciarSesion("ana@test.cl", "x")

        assertTrue(viewModel.loginState.value?.exceptionOrNull() is IOException)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `login ignora llamadas concurrentes`() {
        repository.gate = CompletableDeferred()

        viewModel.iniciarSesion("ana@test.cl", "x")
        viewModel.iniciarSesion("otra@test.cl", "y")

        assertEquals(1, repository.loginCalls)
        assertTrue(viewModel.isLoading.value)
    }

    @Test
    fun `registra un usuario`() {
        viewModel.registrar(TestData.usuarioRequest)

        assertEquals(TestData.usuarioResponse, viewModel.registroState.value?.getOrNull())
        assertEquals(TestData.usuarioRequest, repository.lastRegistrarRequest)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `registro fallido y excepcion`() {
        repository.registrarResult = Result.failure(IllegalStateException("correo duplicado"))
        viewModel.registrar(TestData.usuarioRequest)
        assertEquals(
            "correo duplicado",
            viewModel.registroState.value?.exceptionOrNull()?.message
        )

        repository.throwable = IOException("red")
        viewModel.registrar(TestData.usuarioRequest)
        assertTrue(viewModel.registroState.value?.exceptionOrNull() is IOException)
    }

    @Test
    fun `registro ignora llamadas concurrentes`() {
        repository.gate = CompletableDeferred()

        viewModel.registrar(TestData.usuarioRequest)
        viewModel.registrar(TestData.usuarioRequest)

        assertEquals(1, repository.registrarCalls)
    }

    @Test
    fun `obtenerPerfil sin token no realiza peticion`() {
        viewModel.obtenerPerfil()

        assertEquals(0, repository.perfilCalls)
        assertNull(viewModel.perfilState.value)
    }

    @Test
    fun `obtenerPerfil usa el token guardado`() {
        repository.iniciarSesionResult = Result.success(TestData.loginResponse)
        viewModel.iniciarSesion("ana@test.cl", "x")

        repository.perfilCalls = 0
        repository.perfilResult = Result.failure(IOException("perfil"))

        viewModel.obtenerPerfil()

        assertEquals(1, repository.perfilCalls)
        assertTrue(viewModel.perfilState.value?.isFailure == true)
    }

    @Test
    fun `actualizarPerfil sin sesion devuelve IllegalStateException`() {
        viewModel.actualizarPerfil(TestData.usuarioUpdateRequest)

        val error = viewModel.actualizacionState.value?.exceptionOrNull()
        assertTrue(error is IllegalStateException)
        assertEquals("No hay una sesi\u00f3n activa", error?.message)
        assertEquals(0, repository.actualizarCalls)
    }

    @Test
    fun `actualizarPerfil exitoso actualiza el perfil`() {
        repository.iniciarSesionResult = Result.success(TestData.loginResponse)
        viewModel.iniciarSesion("ana@test.cl", "x")

        val actualizado = TestData.usuarioResponse.copy(telefono = "+56 9 00000000")
        repository.actualizarResult = Result.success(actualizado)

        viewModel.actualizarPerfil(TestData.usuarioUpdateRequest)

        assertEquals(actualizado, viewModel.actualizacionState.value?.getOrNull())
        assertEquals(actualizado, viewModel.perfilState.value?.getOrNull())
        assertEquals("jwt-token", repository.lastActualizarToken)
        assertEquals(TestData.usuarioUpdateRequest, repository.lastActualizarRequest)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `actualizarPerfil fallido no toca el perfil y captura excepciones`() {
        repository.iniciarSesionResult = Result.success(TestData.loginResponse)
        viewModel.iniciarSesion("ana@test.cl", "x")
        val perfilPrevio = viewModel.perfilState.value

        repository.actualizarResult = Result.failure(IllegalStateException("no autorizado"))
        viewModel.actualizarPerfil(TestData.usuarioUpdateRequest)
        assertEquals(
            "no autorizado",
            viewModel.actualizacionState.value?.exceptionOrNull()?.message
        )
        assertEquals(perfilPrevio, viewModel.perfilState.value)

        repository.throwable = IOException("red")
        viewModel.actualizarPerfil(TestData.usuarioUpdateRequest)
        assertTrue(viewModel.actualizacionState.value?.exceptionOrNull() is IOException)
    }

    @Test
    fun `actualizarPerfil ignora llamadas concurrentes`() {
        repository.iniciarSesionResult = Result.success(TestData.loginResponse)
        viewModel.iniciarSesion("ana@test.cl", "x")
        repository.gate = CompletableDeferred()

        viewModel.actualizarPerfil(TestData.usuarioUpdateRequest)
        viewModel.actualizarPerfil(TestData.usuarioUpdateRequest)

        assertEquals(1, repository.actualizarCalls)
    }

    @Test
    fun `solicita recuperacion recortando el correo`() {
        viewModel.solicitarRecuperacion("  ana@test.cl  ")

        assertEquals(Unit, viewModel.recuperacionState.value?.getOrNull())
        assertEquals("ana@test.cl", repository.lastRecuperacionRequest?.correo)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `recuperacion fallida y excepcion`() {
        repository.recuperacionResult = Result.failure(IllegalStateException("limite"))
        viewModel.solicitarRecuperacion("ana@test.cl")
        assertEquals("limite", viewModel.recuperacionState.value?.exceptionOrNull()?.message)

        repository.throwable = IOException("red")
        viewModel.solicitarRecuperacion("ana@test.cl")
        assertTrue(viewModel.recuperacionState.value?.exceptionOrNull() is IOException)
    }

    @Test
    fun `restablece la password`() {
        viewModel.restablecerPassword("reset-token", "nuevaPassword123")

        assertEquals(Unit, viewModel.restablecimientoState.value?.getOrNull())
        assertEquals("reset-token", repository.lastRestablecerRequest?.token)
        assertEquals("nuevaPassword123", repository.lastRestablecerRequest?.nuevaPassword)
    }

    @Test
    fun `restablecimiento fallido y excepcion`() {
        repository.restablecerResult = Result.failure(IllegalStateException("token vencido"))
        viewModel.restablecerPassword("t", "password123")
        assertEquals(
            "token vencido",
            viewModel.restablecimientoState.value?.exceptionOrNull()?.message
        )

        repository.throwable = IOException("red")
        viewModel.restablecerPassword("t", "password123")
        assertTrue(viewModel.restablecimientoState.value?.exceptionOrNull() is IOException)
    }

    @Test
    fun `una CancellationException no deja loading activo`() {
        repository.throwable = CancellationException("cancelado")

        viewModel.registrar(TestData.usuarioRequest)

        assertFalse(viewModel.isLoading.value)
        assertNull(viewModel.registroState.value)
    }

    @Test
    fun `limpiarLogin borra token, perfil y estados de sesion`() {
        repository.iniciarSesionResult = Result.success(TestData.loginResponse)
        viewModel.iniciarSesion("ana@test.cl", "x")

        viewModel.limpiarLogin()

        assertNull(viewModel.loginState.value)
        assertNull(viewModel.token.value)
        assertNull(viewModel.perfilState.value)
        assertNull(viewModel.actualizacionState.value)
    }

    @Test
    fun `limpiar estados individuales`() {
        viewModel.registrar(TestData.usuarioRequest)
        viewModel.solicitarRecuperacion("ana@test.cl")
        viewModel.restablecerPassword("t", "password123")

        viewModel.limpiarRegistro()
        viewModel.limpiarRecuperacion()
        viewModel.limpiarRestablecimiento()
        viewModel.limpiarActualizacion()

        assertNull(viewModel.registroState.value)
        assertNull(viewModel.recuperacionState.value)
        assertNull(viewModel.restablecimientoState.value)
        assertNull(viewModel.actualizacionState.value)
    }
}
