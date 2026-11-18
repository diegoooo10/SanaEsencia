
package com.example.sanaescencia.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sanaescencia.data.model.Especialidad
import com.example.sanaescencia.data.model.Servicio
import com.example.sanaescencia.data.repository.ServicioRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ServicioViewModel(
    private val repository: ServicioRepository
) : ViewModel() {

    private val _especialidadesState =
        MutableStateFlow<Result<List<Especialidad>>?>(null)

    val especialidadesState: StateFlow<Result<List<Especialidad>>?> =
        _especialidadesState.asStateFlow()

    private val _serviciosState =
        MutableStateFlow<Result<List<Servicio>>?>(null)

    val serviciosState: StateFlow<Result<List<Servicio>>?> =
        _serviciosState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)

    val isLoading: StateFlow<Boolean> =
        _isLoading.asStateFlow()

    fun obtenerEspecialidades(token: String) {

        if (_isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true
            _especialidadesState.value = null

            try {
                _especialidadesState.value =
                    repository.obtenerEspecialidades(token)

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _especialidadesState.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun obtenerServicios(
        token: String,
        especialidadId: Int? = null
    ) {
        if (_isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true
            _serviciosState.value = null

            try {
                _serviciosState.value =
                    repository.obtenerServicios(token, especialidadId)

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _serviciosState.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun limpiarEspecialidades() {
        _especialidadesState.value = null
    }

    fun limpiarServicios() {
        _serviciosState.value = null
    }


    fun cargarDatos(token: String) {

        if (_isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true
            _especialidadesState.value = null
            _serviciosState.value = null

            try {
                val especialidades =
                    repository.obtenerEspecialidades(token)

                _especialidadesState.value = especialidades

                if (especialidades.isSuccess) {
                    _serviciosState.value =
                        repository.obtenerServicios(token)
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _serviciosState.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

}
