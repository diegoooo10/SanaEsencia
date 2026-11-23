package com.example.sanaescencia.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.sanaescencia.testutil.FakeProfesionalRepository
import com.example.sanaescencia.testutil.FakeReservaRepository
import com.example.sanaescencia.testutil.FakeServicioRepository
import com.example.sanaescencia.testutil.FakeUsuarioRepository
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class ViewModelFactoriesTest {

    private class ViewModelDesconocido : ViewModel()

    @Test
    fun `ProfesionalViewModelFactory crea su ViewModel`() {
        val factory = ProfesionalViewModelFactory(FakeProfesionalRepository())

        val viewModel = factory.create(ProfesionalViewModel::class.java)

        assertTrue(viewModel is ProfesionalViewModel)
    }

    @Test
    fun `ProfesionalViewModelFactory rechaza otras clases`() {
        val factory = ProfesionalViewModelFactory(FakeProfesionalRepository())

        val error = assertThrows(IllegalArgumentException::class.java) {
            factory.create(ViewModelDesconocido::class.java)
        }

        assertTrue(error.message.orEmpty().contains("ViewModel desconocido"))
    }

    @Test
    fun `ServicioViewModelFactory crea su ViewModel`() {
        val factory = ServicioViewModelFactory(FakeServicioRepository())

        assertTrue(factory.create(ServicioViewModel::class.java) is ServicioViewModel)
    }

    @Test
    fun `ServicioViewModelFactory rechaza otras clases`() {
        val factory = ServicioViewModelFactory(FakeServicioRepository())

        assertThrows(IllegalArgumentException::class.java) {
            factory.create(UsuarioViewModel::class.java)
        }
    }

    @Test
    fun `ReservaViewModelFactory crea su ViewModel`() {
        val factory = ReservaViewModelFactory(FakeReservaRepository())

        assertTrue(factory.create(ReservaViewModel::class.java) is ReservaViewModel)
    }

    @Test
    fun `ReservaViewModelFactory rechaza otras clases`() {
        val factory = ReservaViewModelFactory(FakeReservaRepository())

        val error = assertThrows(IllegalArgumentException::class.java) {
            factory.create(ViewModelDesconocido::class.java)
        }

        assertTrue(error.message.orEmpty().contains("Unknown ViewModel class"))
    }

    @Test
    fun `UsuarioViewModelFactory crea su ViewModel`() {
        val factory = UsuarioViewModelFactory(FakeUsuarioRepository())

        assertTrue(factory.create(UsuarioViewModel::class.java) is UsuarioViewModel)
    }

    @Test
    fun `UsuarioViewModelFactory rechaza otras clases`() {
        val factory = UsuarioViewModelFactory(FakeUsuarioRepository())

        assertThrows(IllegalArgumentException::class.java) {
            factory.create(ProfesionalViewModel::class.java)
        }
    }
}
