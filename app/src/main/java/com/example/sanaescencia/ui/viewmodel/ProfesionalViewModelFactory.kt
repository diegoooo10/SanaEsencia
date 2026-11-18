
package com.example.sanaescencia.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sanaescencia.data.repository.ProfesionalRepository

class ProfesionalViewModelFactory(
    private val repository: ProfesionalRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(ProfesionalViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfesionalViewModel(repository) as T
        }

        throw IllegalArgumentException(
            "ViewModel desconocido: ${modelClass.name}"
        )
    }
}
