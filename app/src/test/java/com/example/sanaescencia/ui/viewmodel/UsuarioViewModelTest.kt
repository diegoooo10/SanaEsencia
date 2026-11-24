package com.example.sanaescencia.ui.viewmodel

import com.example.sanaescencia.data.remote.dto.LoginRequest
import com.example.sanaescencia.data.remote.dto.RecuperacionPasswordRequest
import com.example.sanaescencia.data.remote.dto.RestablecerPasswordRequest
import com.example.sanaescencia.data.remote.dto.UsuarioRequest
import com.example.sanaescencia.data.remote.dto.UsuarioUpdateRequest
import com.example.sanaescencia.data.repository.UsuarioRepository
import com.example.sanaescencia.testutil.MainDispatcherController
import com.example.sanaescencia.testutil.TestData
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import java.io.IOException

class UsuarioViewModelTest : FunSpec({

    val mainDispatcher = MainDispatcherController()

    lateinit var repository: UsuarioRepository
    lateinit var viewModel: UsuarioViewModel

    beforeTest {
        mainDispatcher.install()
        repository = mockk()
        viewModel = UsuarioViewModel(repository)
    }

    afterTest {
        mainDispatcher.reset()
    }

    test("estados iniciales vacios") {
        viewModel.loginState.value shouldBe null
        viewModel.token.value shouldBe null
        viewModel.perfilState.value shouldBe null
        viewModel.actualizacionState.value shouldBe null
        viewModel.registroState.value shouldBe null
        viewModel.recuperacionState.value shouldBe null
        viewModel.restablecimientoState.value shouldBe null
        viewModel.isLoading.value shouldBe false
    }

    test("inicia sesion, guarda el token y carga el perfil") {
        val loginCapturado = slot<LoginRequest>()
        coEvery { repository.iniciarSesion(capture(loginCapturado)) } returns
            Result.success(TestData.loginResponse)
        coEvery { repository.obtenerPerfil("jwt-token") } returns
            Result.success(TestData.usuarioResponse)

        viewModel.iniciarSesion("  ana@test.cl  ", "password123")

        viewModel.loginState.value?.getOrNull() shouldBe TestData.loginResponse
        viewModel.token.value shouldBe "jwt-token"
        viewModel.perfilState.value?.getOrNull() shouldBe TestData.usuarioResponse
        loginCapturado.captured.correo shouldBe "ana@test.cl"
        loginCapturado.captured.password shouldBe "password123"
        coVerify(exactly = 1) { repository.obtenerPerfil("jwt-token") }
        viewModel.isLoading.value shouldBe false
    }

    test("login fallido no guarda token ni consulta perfil") {
        val error = IllegalStateException("credenciales")
        coEvery { repository.iniciarSesion(any()) } returns Result.failure(error)

        viewModel.iniciarSesion("ana@test.cl", "mala")

        viewModel.loginState.value?.exceptionOrNull() shouldBe error
        viewModel.token.value shouldBe null
        coVerify(exactly = 0) { repository.obtenerPerfil(any()) }
    }

    test("login captura excepciones") {
        coEvery { repository.iniciarSesion(any()) } throws IOException("red")

        viewModel.iniciarSesion("ana@test.cl", "x")

        viewModel.loginState.value?.exceptionOrNull().shouldBeInstanceOf<IOException>()
        viewModel.isLoading.value shouldBe false
    }

    test("login ignora llamadas concurrentes") {
        val gate = CompletableDeferred<Unit>()
        coEvery { repository.iniciarSesion(any()) } coAnswers {
            gate.await()
            Result.success(TestData.loginResponse)
        }

        viewModel.iniciarSesion("ana@test.cl", "x")
        viewModel.iniciarSesion("otra@test.cl", "y")

        coVerify(exactly = 1) { repository.iniciarSesion(any()) }
        viewModel.isLoading.value shouldBe true
    }

    test("registra un usuario") {
        val capturado = slot<UsuarioRequest>()
        coEvery { repository.registrar(capture(capturado)) } returns
            Result.success(TestData.usuarioResponse)

        viewModel.registrar(TestData.usuarioRequest)

        viewModel.registroState.value?.getOrNull() shouldBe TestData.usuarioResponse
        capturado.captured shouldBe TestData.usuarioRequest
        viewModel.isLoading.value shouldBe false
    }

    test("registro fallido y excepcion") {
        coEvery { repository.registrar(any()) } returns
            Result.failure(IllegalStateException("correo duplicado")) andThenThrows
            IOException("red")

        viewModel.registrar(TestData.usuarioRequest)
        viewModel.registroState.value?.exceptionOrNull()?.message shouldBe "correo duplicado"

        viewModel.registrar(TestData.usuarioRequest)
        viewModel.registroState.value?.exceptionOrNull().shouldBeInstanceOf<IOException>()
    }

    test("registro ignora llamadas concurrentes") {
        val gate = CompletableDeferred<Unit>()
        coEvery { repository.registrar(any()) } coAnswers {
            gate.await()
            Result.success(TestData.usuarioResponse)
        }

        viewModel.registrar(TestData.usuarioRequest)
        viewModel.registrar(TestData.usuarioRequest)

        coVerify(exactly = 1) { repository.registrar(any()) }
    }

    test("obtenerPerfil sin token no realiza peticion") {
        viewModel.obtenerPerfil()

        coVerify(exactly = 0) { repository.obtenerPerfil(any()) }
        viewModel.perfilState.value shouldBe null
    }

    test("obtenerPerfil usa el token guardado") {
        coEvery { repository.iniciarSesion(any()) } returns
            Result.success(TestData.loginResponse)
        coEvery { repository.obtenerPerfil("jwt-token") } returns
            Result.success(TestData.usuarioResponse) andThen
            Result.failure(IOException("perfil"))

        viewModel.iniciarSesion("ana@test.cl", "x")
        viewModel.obtenerPerfil()

        coVerify(exactly = 2) { repository.obtenerPerfil("jwt-token") }
        viewModel.perfilState.value?.isFailure shouldBe true
    }

    test("actualizarPerfil sin sesion devuelve IllegalStateException") {
        viewModel.actualizarPerfil(TestData.usuarioUpdateRequest)

        val error = viewModel.actualizacionState.value?.exceptionOrNull()
        error.shouldBeInstanceOf<IllegalStateException>()
        error?.message shouldBe "No hay una sesi\u00f3n activa"
        coVerify(exactly = 0) { repository.actualizarPerfil(any(), any()) }
    }

    test("actualizarPerfil exitoso actualiza el perfil") {
        coEvery { repository.iniciarSesion(any()) } returns
            Result.success(TestData.loginResponse)
        coEvery { repository.obtenerPerfil(any()) } returns
            Result.success(TestData.usuarioResponse)

        viewModel.iniciarSesion("ana@test.cl", "x")

        val actualizado = TestData.usuarioResponse.copy(telefono = "+56 9 00000000")
        val capturado = slot<UsuarioUpdateRequest>()
        coEvery { repository.actualizarPerfil("jwt-token", capture(capturado)) } returns
            Result.success(actualizado)

        viewModel.actualizarPerfil(TestData.usuarioUpdateRequest)

        viewModel.actualizacionState.value?.getOrNull() shouldBe actualizado
        viewModel.perfilState.value?.getOrNull() shouldBe actualizado
        capturado.captured shouldBe TestData.usuarioUpdateRequest
        viewModel.isLoading.value shouldBe false
    }

    test("actualizarPerfil fallido no toca el perfil y captura excepciones") {
        coEvery { repository.iniciarSesion(any()) } returns
            Result.success(TestData.loginResponse)
        coEvery { repository.obtenerPerfil(any()) } returns
            Result.success(TestData.usuarioResponse)

        viewModel.iniciarSesion("ana@test.cl", "x")
        val perfilPrevio = viewModel.perfilState.value

        coEvery { repository.actualizarPerfil(any(), any()) } returns
            Result.failure(IllegalStateException("no autorizado")) andThenThrows
            IOException("red")

        viewModel.actualizarPerfil(TestData.usuarioUpdateRequest)
        viewModel.actualizacionState.value?.exceptionOrNull()?.message shouldBe "no autorizado"
        viewModel.perfilState.value shouldBe perfilPrevio

        viewModel.actualizarPerfil(TestData.usuarioUpdateRequest)
        viewModel.actualizacionState.value?.exceptionOrNull().shouldBeInstanceOf<IOException>()
    }

    test("actualizarPerfil ignora llamadas concurrentes") {
        coEvery { repository.iniciarSesion(any()) } returns
            Result.success(TestData.loginResponse)
        coEvery { repository.obtenerPerfil(any()) } returns
            Result.success(TestData.usuarioResponse)

        viewModel.iniciarSesion("ana@test.cl", "x")

        val gate = CompletableDeferred<Unit>()
        coEvery { repository.actualizarPerfil(any(), any()) } coAnswers {
            gate.await()
            Result.success(TestData.usuarioResponse)
        }

        viewModel.actualizarPerfil(TestData.usuarioUpdateRequest)
        viewModel.actualizarPerfil(TestData.usuarioUpdateRequest)

        coVerify(exactly = 1) { repository.actualizarPerfil(any(), any()) }
    }

    test("solicita recuperacion recortando el correo") {
        val capturado = slot<RecuperacionPasswordRequest>()
        coEvery { repository.solicitarRecuperacion(capture(capturado)) } returns
            Result.success(Unit)

        viewModel.solicitarRecuperacion("  ana@test.cl  ")

        viewModel.recuperacionState.value?.getOrNull() shouldBe Unit
        capturado.captured.correo shouldBe "ana@test.cl"
        viewModel.isLoading.value shouldBe false
    }

    test("recuperacion fallida y excepcion") {
        coEvery { repository.solicitarRecuperacion(any()) } returns
            Result.failure(IllegalStateException("limite")) andThenThrows
            IOException("red")

        viewModel.solicitarRecuperacion("ana@test.cl")
        viewModel.recuperacionState.value?.exceptionOrNull()?.message shouldBe "limite"

        viewModel.solicitarRecuperacion("ana@test.cl")
        viewModel.recuperacionState.value?.exceptionOrNull().shouldBeInstanceOf<IOException>()
    }

    test("restablece la password") {
        val capturado = slot<RestablecerPasswordRequest>()
        coEvery { repository.restablecerPassword(capture(capturado)) } returns
            Result.success(Unit)

        viewModel.restablecerPassword("reset-token", "nuevaPassword123")

        viewModel.restablecimientoState.value?.getOrNull() shouldBe Unit
        capturado.captured.token shouldBe "reset-token"
        capturado.captured.nuevaPassword shouldBe "nuevaPassword123"
    }

    test("restablecimiento fallido y excepcion") {
        coEvery { repository.restablecerPassword(any()) } returns
            Result.failure(IllegalStateException("token vencido")) andThenThrows
            IOException("red")

        viewModel.restablecerPassword("t", "password123")
        viewModel.restablecimientoState.value?.exceptionOrNull()?.message shouldBe
            "token vencido"

        viewModel.restablecerPassword("t", "password123")
        viewModel.restablecimientoState.value?.exceptionOrNull()
            .shouldBeInstanceOf<IOException>()
    }

    test("una CancellationException no deja loading activo") {
        coEvery { repository.registrar(any()) } throws CancellationException("cancelado")

        viewModel.registrar(TestData.usuarioRequest)

        viewModel.isLoading.value shouldBe false
        viewModel.registroState.value shouldBe null
    }

    test("limpiarLogin borra token, perfil y estados de sesion") {
        coEvery { repository.iniciarSesion(any()) } returns
            Result.success(TestData.loginResponse)
        coEvery { repository.obtenerPerfil(any()) } returns
            Result.success(TestData.usuarioResponse)

        viewModel.iniciarSesion("ana@test.cl", "x")
        viewModel.limpiarLogin()

        viewModel.loginState.value shouldBe null
        viewModel.token.value shouldBe null
        viewModel.perfilState.value shouldBe null
        viewModel.actualizacionState.value shouldBe null
    }

    test("limpiar estados individuales") {
        coEvery { repository.registrar(any()) } returns
            Result.success(TestData.usuarioResponse)
        coEvery { repository.solicitarRecuperacion(any()) } returns Result.success(Unit)
        coEvery { repository.restablecerPassword(any()) } returns Result.success(Unit)

        viewModel.registrar(TestData.usuarioRequest)
        viewModel.solicitarRecuperacion("ana@test.cl")
        viewModel.restablecerPassword("t", "password123")

        viewModel.limpiarRegistro()
        viewModel.limpiarRecuperacion()
        viewModel.limpiarRestablecimiento()
        viewModel.limpiarActualizacion()

        viewModel.registroState.value shouldBe null
        viewModel.recuperacionState.value shouldBe null
        viewModel.restablecimientoState.value shouldBe null
        viewModel.actualizacionState.value shouldBe null
    }
})
