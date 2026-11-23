package com.example.sanaescencia.ui.viewmodel

import com.example.sanaescencia.testutil.FakeServicioRepository
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

class ServicioViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeServicioRepository()
    private val viewModel = ServicioViewModel(repository)

    @Test
    fun `estado inicial vacio y sin carga`() {
        assertNull(viewModel.especialidadesState.value)
        assertNull(viewModel.serviciosState.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `carga especialidades exitosamente`() {
        repository.especialidadesResult = Result.success(listOf(TestData.especialidad))

        viewModel.obtenerEspecialidades("tk")

        assertEquals(
            listOf(TestData.especialidad),
            viewModel.especialidadesState.value?.getOrNull()
        )
        assertEquals("tk", repository.lastEspecialidadesToken)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `especialidades con failure se publican`() {
        val error = RuntimeException("sin datos")
        repository.especialidadesResult = Result.failure(error)

        viewModel.obtenerEspecialidades("tk")

        assertEquals(error, viewModel.especialidadesState.value?.exceptionOrNull())
    }

    @Test
    fun `especialidades capturan excepciones`() {
        repository.throwable = IOException("red")

        viewModel.obtenerEspecialidades("tk")

        assertTrue(viewModel.especialidadesState.value?.exceptionOrNull() is IOException)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `carga servicios con y sin filtro`() {
        viewModel.obtenerServicios("tk")
        assertTrue(repository.serviciosFilterWasNull)
        assertEquals(1, repository.serviciosCalls)

        viewModel.obtenerServicios("tk", 3)
        assertEquals(3, repository.lastEspecialidadId)
        assertEquals(2, repository.serviciosCalls)
    }

    @Test
    fun `servicios con failure se publican`() {
        repository.serviciosResult = Result.failure(IllegalStateException("nada"))

        viewModel.obtenerServicios("tk", 1)

        assertEquals("nada", viewModel.serviciosState.value?.exceptionOrNull()?.message)
    }

    @Test
    fun `servicios capturan excepciones`() {
        repository.throwable = IOException("red")

        viewModel.obtenerServicios("tk", 1)

        assertTrue(viewModel.serviciosState.value?.exceptionOrNull() is IOException)
    }

    @Test
    fun `ignora llamadas concurrentes`() {
        val gate = CompletableDeferred<Unit>()
        repository.gate = gate

        viewModel.obtenerEspecialidades("tk")
        viewModel.obtenerServicios("tk")

        assertEquals(1, repository.especialidadesCalls)
        assertEquals(0, repository.serviciosCalls)
        assertTrue(viewModel.isLoading.value)

        gate.complete(Unit)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `cargarDatos obtiene especialidades y luego servicios`() {
        viewModel.cargarDatos("tk")

        assertEquals(1, repository.especialidadesCalls)
        assertEquals(1, repository.serviciosCalls)
        assertTrue(viewModel.especialidadesState.value?.isSuccess == true)
        assertTrue(viewModel.serviciosState.value?.isSuccess == true)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `cargarDatos no pide servicios si fallan las especialidades`() {
        repository.especialidadesResult = Result.failure(IOException("caido"))

        viewModel.cargarDatos("tk")

        assertTrue(viewModel.especialidadesState.value?.isFailure == true)
        assertNull(viewModel.serviciosState.value)
        assertEquals(0, repository.serviciosCalls)
    }

    @Test
    fun `cargarDatos convierte una excepcion en failure de servicios`() {
        repository.throwable = IOException("boom")

        viewModel.cargarDatos("tk")

        assertTrue(viewModel.serviciosState.value?.isFailure == true)
        assertNull(viewModel.especialidadesState.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `cargarDatos ignora llamadas concurrentes`() {
        val gate = CompletableDeferred<Unit>()
        repository.gate = gate

        viewModel.cargarDatos("tk")
        viewModel.cargarDatos("tk-2")

        assertEquals(1, repository.especialidadesCalls)

        gate.complete(Unit)
    }

    @Test
    fun `una CancellationException no deja loading activo`() {
        repository.throwable = CancellationException("cancelado")

        viewModel.obtenerEspecialidades("tk")

        assertFalse(viewModel.isLoading.value)
        assertNull(viewModel.especialidadesState.value)
    }

    @Test
    fun `limpiar estados`() {
        viewModel.obtenerEspecialidades("tk")
        viewModel.obtenerServicios("tk")

        viewModel.limpiarEspecialidades()
        viewModel.limpiarServicios()

        assertNull(viewModel.especialidadesState.value)
        assertNull(viewModel.serviciosState.value)
    }
}
