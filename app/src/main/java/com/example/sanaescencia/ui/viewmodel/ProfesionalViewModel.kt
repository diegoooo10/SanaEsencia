
package com.example.sanaescencia.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sanaescencia.data.model.Profesional
import com.example.sanaescencia.data.repository.ProfesionalRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfesionalViewModel(
    private val repository: ProfesionalRepository
) : ViewModel() {

    private val _profesionalesState =
        MutableStateFlow<Result<List<Profesional>>?>(null)

    val profesionalesState: StateFlow<Result<List<Profesional>>?> =
        _profesionalesState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)

    val isLoading: StateFlow<Boolean> =
        _isLoading.asStateFlow()

    fun obtenerProfesionales(token: String) {

        if (_isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true
            _profesionalesState.value = null

            try {
                _profesionalesState.value =
                    repository.obtenerProfesionales(token)

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _profesionalesState.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun limpiarProfesionales() {
        _profesionalesState.value = null
    }
}
