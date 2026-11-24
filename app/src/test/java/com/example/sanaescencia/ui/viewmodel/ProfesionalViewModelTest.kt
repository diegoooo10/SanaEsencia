package com.example.sanaescencia.ui.viewmodel

import com.example.sanaescencia.data.repository.ProfesionalRepository
import com.example.sanaescencia.testutil.MainDispatcherController
import com.example.sanaescencia.testutil.TestData
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import java.io.IOException

class ProfesionalViewModelTest : FunSpec({

    val mainDispatcher = MainDispatcherController()

    lateinit var repository: ProfesionalRepository
    lateinit var viewModel: ProfesionalViewModel

    beforeTest {
        mainDispatcher.install()
        repository = mockk()
        viewModel = ProfesionalViewModel(repository)
    }

    afterTest {
        mainDispatcher.reset()
    }

    test("estado inicial vacio y sin carga") {
        viewModel.profesionalesState.value shouldBe null
        viewModel.isLoading.value shouldBe false
    }

    test("carga los profesionales exitosamente") {
        coEvery { repository.obtenerProfesionales("token-abc") } returns
            Result.success(listOf(TestData.profesional))

        viewModel.obtenerProfesionales("token-abc")

        viewModel.profesionalesState.value?.getOrNull() shouldBe
            listOf(TestData.profesional)
        coVerify(exactly = 1) { repository.obtenerProfesionales("token-abc") }
        viewModel.isLoading.value shouldBe false
    }

    test("propaga un Result failure del repositorio") {
        val error = IllegalStateException("servicio caido")
        coEvery { repository.obtenerProfesionales(any()) } returns Result.failure(error)

        viewModel.obtenerProfesionales("token")

        viewModel.profesionalesState.value?.exceptionOrNull() shouldBe error
    }

    test("convierte excepciones inesperadas en failure") {
        coEvery { repository.obtenerProfesionales(any()) } throws IOException("sin red")

        viewModel.obtenerProfesionales("token")

        viewModel.profesionalesState.value?.isFailure shouldBe true
        viewModel.profesionalesState.value?.exceptionOrNull()
            .shouldBeInstanceOf<IOException>()
        viewModel.isLoading.value shouldBe false
    }

    test("marca loading mientras la peticion esta en curso") {
        val gate = CompletableDeferred<Unit>()
        coEvery { repository.obtenerProfesionales(any()) } coAnswers {
            gate.await()
            Result.success(listOf(TestData.profesional))
        }

        viewModel.obtenerProfesionales("token")

        viewModel.isLoading.value shouldBe true

        gate.complete(Unit)

        viewModel.isLoading.value shouldBe false
        viewModel.profesionalesState.value?.isSuccess shouldBe true
    }

    test("ignora llamadas concurrentes mientras esta cargando") {
        val gate = CompletableDeferred<Unit>()
        coEvery { repository.obtenerProfesionales(any()) } coAnswers {
            gate.await()
            Result.success(listOf(TestData.profesional))
        }

        viewModel.obtenerProfesionales("token-1")
        viewModel.obtenerProfesionales("token-2")

        coVerify(exactly = 1) { repository.obtenerProfesionales(any()) }
        coVerify(exactly = 1) { repository.obtenerProfesionales("token-1") }
        coVerify(exactly = 0) { repository.obtenerProfesionales("token-2") }
    }

    test("una CancellationException deja de cargar y no publica estado") {
        coEvery { repository.obtenerProfesionales(any()) } throws
            CancellationException("cancelado")

        viewModel.obtenerProfesionales("token")

        viewModel.isLoading.value shouldBe false
        viewModel.profesionalesState.value shouldBe null
    }

    test("limpiar deja el estado en null") {
        coEvery { repository.obtenerProfesionales(any()) } returns
            Result.success(listOf(TestData.profesional))

        viewModel.obtenerProfesionales("token")
        viewModel.profesionalesState.value shouldBe
            Result.success(listOf(TestData.profesional))

        viewModel.limpiarProfesionales()

        viewModel.profesionalesState.value shouldBe null
    }
})
