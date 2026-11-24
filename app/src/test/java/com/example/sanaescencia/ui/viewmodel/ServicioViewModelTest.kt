package com.example.sanaescencia.ui.viewmodel

import com.example.sanaescencia.data.repository.ServicioRepository
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

class ServicioViewModelTest : FunSpec({

    val mainDispatcher = MainDispatcherController()

    lateinit var repository: ServicioRepository
    lateinit var viewModel: ServicioViewModel

    beforeTest {
        mainDispatcher.install()
        repository = mockk()
        viewModel = ServicioViewModel(repository)
    }

    afterTest {
        mainDispatcher.reset()
    }

    test("estado inicial vacio y sin carga") {
        viewModel.especialidadesState.value shouldBe null
        viewModel.serviciosState.value shouldBe null
        viewModel.isLoading.value shouldBe false
    }

    test("carga especialidades exitosamente") {
        coEvery { repository.obtenerEspecialidades("tk") } returns
            Result.success(listOf(TestData.especialidad))

        viewModel.obtenerEspecialidades("tk")

        viewModel.especialidadesState.value?.getOrNull() shouldBe
            listOf(TestData.especialidad)
        coVerify(exactly = 1) { repository.obtenerEspecialidades("tk") }
        viewModel.isLoading.value shouldBe false
    }

    test("especialidades con failure se publican") {
        val error = RuntimeException("sin datos")
        coEvery { repository.obtenerEspecialidades(any()) } returns Result.failure(error)

        viewModel.obtenerEspecialidades("tk")

        viewModel.especialidadesState.value?.exceptionOrNull() shouldBe error
    }

    test("especialidades capturan excepciones") {
        coEvery { repository.obtenerEspecialidades(any()) } throws IOException("red")

        viewModel.obtenerEspecialidades("tk")

        viewModel.especialidadesState.value?.exceptionOrNull()
            .shouldBeInstanceOf<IOException>()
        viewModel.isLoading.value shouldBe false
    }

    test("carga servicios con y sin filtro") {
        coEvery { repository.obtenerServicios("tk", null) } returns
            Result.success(listOf(TestData.servicio))
        coEvery { repository.obtenerServicios("tk", 3) } returns
            Result.success(listOf(TestData.servicio))

        viewModel.obtenerServicios("tk")
        coVerify(exactly = 1) { repository.obtenerServicios("tk", null) }

        viewModel.obtenerServicios("tk", 3)
        coVerify(exactly = 1) { repository.obtenerServicios("tk", 3) }
        coVerify(exactly = 2) { repository.obtenerServicios("tk", any()) }
    }

    test("servicios con failure se publican") {
        coEvery { repository.obtenerServicios(any(), any()) } returns
            Result.failure(IllegalStateException("nada"))

        viewModel.obtenerServicios("tk", 1)

        viewModel.serviciosState.value?.exceptionOrNull()?.message shouldBe "nada"
    }

    test("servicios capturan excepciones") {
        coEvery { repository.obtenerServicios(any(), any()) } throws IOException("red")

        viewModel.obtenerServicios("tk", 1)

        viewModel.serviciosState.value?.exceptionOrNull().shouldBeInstanceOf<IOException>()
    }

    test("ignora llamadas concurrentes") {
        val gate = CompletableDeferred<Unit>()
        coEvery { repository.obtenerEspecialidades(any()) } coAnswers {
            gate.await()
            Result.success(listOf(TestData.especialidad))
        }

        viewModel.obtenerEspecialidades("tk")
        viewModel.obtenerServicios("tk")

        coVerify(exactly = 1) { repository.obtenerEspecialidades(any()) }
        coVerify(exactly = 0) { repository.obtenerServicios(any(), any()) }
        viewModel.isLoading.value shouldBe true

        gate.complete(Unit)

        viewModel.isLoading.value shouldBe false
    }

    test("cargarDatos obtiene especialidades y luego servicios") {
        coEvery { repository.obtenerEspecialidades("tk") } returns
            Result.success(listOf(TestData.especialidad))
        coEvery { repository.obtenerServicios("tk", null) } returns
            Result.success(listOf(TestData.servicio))

        viewModel.cargarDatos("tk")

        coVerify(exactly = 1) { repository.obtenerEspecialidades("tk") }
        coVerify(exactly = 1) { repository.obtenerServicios("tk", null) }
        viewModel.especialidadesState.value?.isSuccess shouldBe true
        viewModel.serviciosState.value?.isSuccess shouldBe true
        viewModel.isLoading.value shouldBe false
    }

    test("cargarDatos no pide servicios si fallan las especialidades") {
        coEvery { repository.obtenerEspecialidades(any()) } returns
            Result.failure(IOException("caido"))

        viewModel.cargarDatos("tk")

        viewModel.especialidadesState.value?.isFailure shouldBe true
        viewModel.serviciosState.value shouldBe null
        coVerify(exactly = 0) { repository.obtenerServicios(any(), any()) }
    }

    test("cargarDatos convierte una excepcion en failure de servicios") {
        coEvery { repository.obtenerEspecialidades(any()) } throws IOException("boom")

        viewModel.cargarDatos("tk")

        viewModel.serviciosState.value?.isFailure shouldBe true
        viewModel.especialidadesState.value shouldBe null
        viewModel.isLoading.value shouldBe false
    }

    test("cargarDatos ignora llamadas concurrentes") {
        val gate = CompletableDeferred<Unit>()
        coEvery { repository.obtenerEspecialidades(any()) } coAnswers {
            gate.await()
            Result.success(listOf(TestData.especialidad))
        }

        viewModel.cargarDatos("tk")
        viewModel.cargarDatos("tk-2")

        coVerify(exactly = 1) { repository.obtenerEspecialidades(any()) }

        gate.complete(Unit)
    }

    test("una CancellationException no deja loading activo") {
        coEvery { repository.obtenerEspecialidades(any()) } throws
            CancellationException("cancelado")

        viewModel.obtenerEspecialidades("tk")

        viewModel.isLoading.value shouldBe false
        viewModel.especialidadesState.value shouldBe null
    }

    test("limpiar estados") {
        coEvery { repository.obtenerEspecialidades(any()) } returns
            Result.success(listOf(TestData.especialidad))
        coEvery { repository.obtenerServicios(any(), any()) } returns
            Result.success(listOf(TestData.servicio))

        viewModel.obtenerEspecialidades("tk")
        viewModel.obtenerServicios("tk")

        viewModel.limpiarEspecialidades()
        viewModel.limpiarServicios()

        viewModel.especialidadesState.value shouldBe null
        viewModel.serviciosState.value shouldBe null
    }
})
