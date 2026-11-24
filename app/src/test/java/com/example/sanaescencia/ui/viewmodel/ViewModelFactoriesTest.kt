package com.example.sanaescencia.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.sanaescencia.data.repository.ProfesionalRepository
import com.example.sanaescencia.data.repository.ReservaRepository
import com.example.sanaescencia.data.repository.ServicioRepository
import com.example.sanaescencia.data.repository.UsuarioRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.mockk

class ViewModelFactoriesTest : FunSpec({

    class ViewModelDesconocido : ViewModel()

    test("ProfesionalViewModelFactory crea su ViewModel") {
        val factory = ProfesionalViewModelFactory(mockk<ProfesionalRepository>())

        factory.create(ProfesionalViewModel::class.java)
            .shouldBeInstanceOf<ProfesionalViewModel>()
    }

    test("ProfesionalViewModelFactory rechaza otras clases") {
        val factory = ProfesionalViewModelFactory(mockk<ProfesionalRepository>())

        val error = shouldThrow<IllegalArgumentException> {
            factory.create(ViewModelDesconocido::class.java)
        }

        error.message.orEmpty() shouldContain "ViewModel desconocido"
    }

    test("ServicioViewModelFactory crea su ViewModel") {
        val factory = ServicioViewModelFactory(mockk<ServicioRepository>())

        factory.create(ServicioViewModel::class.java)
            .shouldBeInstanceOf<ServicioViewModel>()
    }

    test("ServicioViewModelFactory rechaza otras clases") {
        val factory = ServicioViewModelFactory(mockk<ServicioRepository>())

        shouldThrow<IllegalArgumentException> {
            factory.create(UsuarioViewModel::class.java)
        }
    }

    test("ReservaViewModelFactory crea su ViewModel") {
        val factory = ReservaViewModelFactory(mockk<ReservaRepository>())

        factory.create(ReservaViewModel::class.java)
            .shouldBeInstanceOf<ReservaViewModel>()
    }

    test("ReservaViewModelFactory rechaza otras clases") {
        val factory = ReservaViewModelFactory(mockk<ReservaRepository>())

        val error = shouldThrow<IllegalArgumentException> {
            factory.create(ViewModelDesconocido::class.java)
        }

        error.message.orEmpty() shouldContain "Unknown ViewModel class"
    }

    test("UsuarioViewModelFactory crea su ViewModel") {
        val factory = UsuarioViewModelFactory(mockk<UsuarioRepository>())

        factory.create(UsuarioViewModel::class.java)
            .shouldBeInstanceOf<UsuarioViewModel>()
    }

    test("UsuarioViewModelFactory rechaza otras clases") {
        val factory = UsuarioViewModelFactory(mockk<UsuarioRepository>())

        shouldThrow<IllegalArgumentException> {
            factory.create(ProfesionalViewModel::class.java)
        }
    }
})
