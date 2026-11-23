package com.example.sanaescencia.ui.viewmodel

import com.example.sanaescencia.testutil.FakeProfesionalRepository
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

class ProfesionalViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeProfesionalRepository()
    private val viewModel = ProfesionalViewModel(repository)

    @Test
    fun `estado inicial vacio y sin carga`() {
        assertNull(viewModel.profesionalesState.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `carga los profesionales exitosamente`() {
        repository.result = Result.success(listOf(TestData.profesional))

        viewModel.obtenerProfesionales("token-abc")

        assertEquals(
            listOf(TestData.profesional),
            viewModel.profesionalesState.value?.getOrNull()
        )
        assertEquals("token-abc", repository.lastToken)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `propaga un Result failure del repositorio`() {
        val error = IllegalStateException("servicio caido")
        repository.result = Result.failure(error)

        viewModel.obtenerProfesionales("token")

        assertEquals(error, viewModel.profesionalesState.value?.exceptionOrNull())
    }

    @Test
    fun `convierte excepciones inesperadas en failure`() {
        repository.throwable = IOException("sin red")

        viewModel.obtenerProfesionales("token")

        assertTrue(viewModel.profesionalesState.value?.isFailure == true)
        assertTrue(viewModel.profesionalesState.value?.exceptionOrNull() is IOException)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `marca loading mientras la peticion esta en curso`() {
        val gate = CompletableDeferred<Unit>()
        repository.gate = gate

        viewModel.obtenerProfesionales("token")

        assertTrue(viewModel.isLoading.value)

        gate.complete(Unit)

        assertFalse(viewModel.isLoading.value)
        assertTrue(viewModel.profesionalesState.value?.isSuccess == true)
    }

    @Test
    fun `ignora llamadas concurrentes mientras esta cargando`() {
        val gate = CompletableDeferred<Unit>()
        repository.gate = gate

        viewModel.obtenerProfesionales("token-1")
        viewModel.obtenerProfesionales("token-2")

        assertEquals(1, repository.calls)
        assertEquals("token-1", repository.lastToken)
    }

    @Test
    fun `una CancellationException deja de cargar y no publica estado`() {
        repository.throwable = CancellationException("cancelado")

        viewModel.obtenerProfesionales("token")

        assertFalse(viewModel.isLoading.value)
        assertNull(viewModel.profesionalesState.value)
    }

    @Test
    fun `limpiar deja el estado en null`() {
        viewModel.obtenerProfesionales("token")
        assertTrue(viewModel.profesionalesState.value != null)

        viewModel.limpiarProfesionales()

        assertNull(viewModel.profesionalesState.value)
    }
}
