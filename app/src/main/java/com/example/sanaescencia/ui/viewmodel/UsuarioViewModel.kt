
package com.example.sanaescencia.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sanaescencia.data.remote.dto.LoginRequest
import com.example.sanaescencia.data.remote.dto.LoginResponse
import com.example.sanaescencia.data.repository.UsuarioRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.sanaescencia.data.remote.dto.UsuarioResponse
import com.example.sanaescencia.data.remote.dto.UsuarioRequest
import com.example.sanaescencia.data.remote.dto.UsuarioUpdateRequest
import com.example.sanaescencia.data.remote.dto.RecuperacionPasswordRequest

class UsuarioViewModel(
    private val repository: UsuarioRepository
) : ViewModel() {

    // Estado del inicio de sesión
    private val _loginState =
        MutableStateFlow<Result<LoginResponse>?>(null)
    private val _token = MutableStateFlow<String?>(null)

    private val _perfilState =
        MutableStateFlow<Result<UsuarioResponse>?>(null)

    // Estado de actualización del perfil
    private val _actualizacionState =
        MutableStateFlow<Result<UsuarioResponse>?>(null)

    val actualizacionState: StateFlow<Result<UsuarioResponse>?> =
        _actualizacionState.asStateFlow()

    val perfilState: StateFlow<Result<UsuarioResponse>?> =
        _perfilState.asStateFlow()

    // Estado del registro de usuarios
    private val _registroState =
        MutableStateFlow<Result<UsuarioResponse>?>(null)

    val registroState: StateFlow<Result<UsuarioResponse>?> =
        _registroState.asStateFlow()

    val token: StateFlow<String?> = _token.asStateFlow()

    val loginState: StateFlow<Result<LoginResponse>?> =
        _loginState.asStateFlow()

    // Estado de carga
    private val _isLoading = MutableStateFlow(false)

    val isLoading: StateFlow<Boolean> =
        _isLoading.asStateFlow()

    // Estado de recuperación de contraseña
    private val _recuperacionState =
        MutableStateFlow<Result<Unit>?>(null)

    val recuperacionState: StateFlow<Result<Unit>?> =
        _recuperacionState.asStateFlow()

    // Iniciar sesión
    fun iniciarSesion(correo: String, password: String) {

        if (_isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true
            _loginState.value = null

            try {
                val request = LoginRequest(
                    correo = correo.trim(),
                    password = password
                )

                val resultado = repository.iniciarSesion(request)

                resultado.onSuccess { respuesta ->
                    _token.value = respuesta.token
                    obtenerPerfil()
                }

                _loginState.value = resultado

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _loginState.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Registrar un nuevo usuario
    fun registrar(request: UsuarioRequest) {

        if (_isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true
            _registroState.value = null

            try {
                _registroState.value = repository.registrar(request)

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _registroState.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Limpiar el resultado del registro
    fun limpiarRegistro() {
        _registroState.value = null
    }

    // Limpiar el estado del inicio de sesión
    fun limpiarLogin() {
        _loginState.value = null
        _token.value = null
        _perfilState.value = null
        _actualizacionState.value = null
    }

    fun obtenerPerfil() {
        val tokenActual = _token.value ?: return

        viewModelScope.launch {
            _perfilState.value = repository.obtenerPerfil(tokenActual)
        }
    }

    // Actualizar información del usuario autenticado
    fun actualizarPerfil(request: UsuarioUpdateRequest) {
        val tokenActual = _token.value ?: run {
            _actualizacionState.value = Result.failure(
                IllegalStateException("No hay una sesión activa")
            )
            return
        }

        if (_isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true
            _actualizacionState.value = null

            try {
                val resultado = repository.actualizarPerfil(
                    tokenActual,
                    request
                )

                resultado.onSuccess { usuarioActualizado ->
                    _perfilState.value = Result.success(usuarioActualizado)
                }

                _actualizacionState.value = resultado

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _actualizacionState.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun limpiarActualizacion() {
        _actualizacionState.value = null
    }


    fun solicitarRecuperacion(correo: String) {

        if (_isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true
            _recuperacionState.value = null

            try {
                val request = RecuperacionPasswordRequest(
                    correo = correo.trim()
                )

                _recuperacionState.value =
                    repository.solicitarRecuperacion(request)

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _recuperacionState.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun limpiarRecuperacion() {
        _recuperacionState.value = null
    }

}
