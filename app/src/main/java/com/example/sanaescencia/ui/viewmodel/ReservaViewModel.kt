
package com.example.sanaescencia.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sanaescencia.data.model.Reserva
import com.example.sanaescencia.data.remote.dto.ReservaRequest
import com.example.sanaescencia.data.repository.ReservaRepository
import com.example.sanaescencia.data.remote.dto.InicioPagoResponse
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

class ReservaViewModel(
    private val repository: ReservaRepository
) : ViewModel() {

    // Estado de disponibilidad
    private val _horariosState =
        MutableStateFlow<Result<List<LocalTime>>?>(null)

    val horariosState: StateFlow<Result<List<LocalTime>>?> =
        _horariosState.asStateFlow()

    // Estado del listado de reservas
    private val _reservasState =
        MutableStateFlow<Result<List<Reserva>>?>(null)

    val reservasState: StateFlow<Result<List<Reserva>>?> =
        _reservasState.asStateFlow()

    // Estado del detalle de una reserva
    private val _reservaDetalleState =
        MutableStateFlow<Result<Reserva>?>(null)

    val reservaDetalleState: StateFlow<Result<Reserva>?> =
        _reservaDetalleState.asStateFlow()

    // Estado de creación de reservas
    private val _crearReservaState =
        MutableStateFlow<Result<Reserva>?>(null)

    val crearReservaState: StateFlow<Result<Reserva>?> =
        _crearReservaState.asStateFlow()

    // Estado de cancelación
    private val _cancelacionState =
        MutableStateFlow<Result<Unit>?>(null)

    val cancelacionState: StateFlow<Result<Unit>?> =
        _cancelacionState.asStateFlow()

    // Estado de inicio de pago
    private val _inicioPagoState =
        MutableStateFlow<Result<InicioPagoResponse>?>(null)

    val inicioPagoState: StateFlow<Result<InicioPagoResponse>?> =
        _inicioPagoState.asStateFlow()

    // Estados de carga
    private val _isLoadingHorarios = MutableStateFlow(false)
    val isLoadingHorarios: StateFlow<Boolean> =
        _isLoadingHorarios.asStateFlow()

    private val _isLoadingReservas = MutableStateFlow(false)
    val isLoadingReservas: StateFlow<Boolean> =
        _isLoadingReservas.asStateFlow()

    private val _isLoadingDetalle = MutableStateFlow(false)
    val isLoadingDetalle: StateFlow<Boolean> =
        _isLoadingDetalle.asStateFlow()

    private val _isLoadingCrear = MutableStateFlow(false)
    val isLoadingCrear: StateFlow<Boolean> =
        _isLoadingCrear.asStateFlow()

    private val _isLoadingCancelacion = MutableStateFlow(false)
    val isLoadingCancelacion: StateFlow<Boolean> =
        _isLoadingCancelacion.asStateFlow()

    private val _isLoadingPago = MutableStateFlow(false)
    val isLoadingPago: StateFlow<Boolean> =
        _isLoadingPago.asStateFlow()


    // Obtener horarios disponibles
    fun obtenerHorariosDisponibles(
        token: String,
        profesionalId: Long,
        servicioId: Long,
        fecha: LocalDate,
        modalidad: String
    ) {

        if (_isLoadingHorarios.value) return

        viewModelScope.launch {
            _isLoadingHorarios.value = true
            _horariosState.value = null

            try {
                _horariosState.value =
                    repository.obtenerHorariosDisponibles(
                        token = token,
                        profesionalId = profesionalId,
                        servicioId = servicioId,
                        fecha = fecha,
                        modalidad = modalidad
                    )

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _horariosState.value = Result.failure(e)
            } finally {
                _isLoadingHorarios.value = false
            }
        }
    }


    // Obtener todas las reservas
    fun obtenerReservas(token: String) {

        if (_isLoadingReservas.value) return

        viewModelScope.launch {
            _isLoadingReservas.value = true
            _reservasState.value = null

            try {
                _reservasState.value =
                    repository.obtenerReservas(token)

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _reservasState.value = Result.failure(e)
            } finally {
                _isLoadingReservas.value = false
            }
        }
    }


    // Obtener una reserva por ID
    fun obtenerReservaPorId(
        token: String,
        id: Long
    ) {

        if (_isLoadingDetalle.value) return

        viewModelScope.launch {
            _isLoadingDetalle.value = true
            _reservaDetalleState.value = null

            try {
                _reservaDetalleState.value =
                    repository.obtenerReservaPorId(
                        token = token,
                        id = id
                    )

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _reservaDetalleState.value = Result.failure(e)
            } finally {
                _isLoadingDetalle.value = false
            }
        }
    }


    // Crear una nueva reserva
    fun crearReserva(
        token: String,
        request: ReservaRequest
    ) {

        if (_isLoadingCrear.value) return

        viewModelScope.launch {
            _isLoadingCrear.value = true
            _crearReservaState.value = null

            try {
                _crearReservaState.value =
                    repository.crearReserva(
                        token = token,
                        request = request
                    )

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _crearReservaState.value = Result.failure(e)
            } finally {
                _isLoadingCrear.value = false
            }
        }
    }


    // Cancelar una reserva
    fun cancelarReserva(
        token: String,
        id: Long
    ) {

        if (_isLoadingCancelacion.value) return

        viewModelScope.launch {
            _isLoadingCancelacion.value = true
            _cancelacionState.value = null

            try {
                _cancelacionState.value =
                    repository.cancelarReserva(
                        token = token,
                        id = id
                    )

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _cancelacionState.value = Result.failure(e)
            } finally {
                _isLoadingCancelacion.value = false
            }
        }
    }


    fun iniciarPago(
        token: String,
        reservaId: Long
    ) {
        if (_isLoadingPago.value) return

        viewModelScope.launch {
            _isLoadingPago.value = true
            _inicioPagoState.value = null

            try {
                _inicioPagoState.value =
                    repository.iniciarPago(
                        token = token,
                        reservaId = reservaId
                    )

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _inicioPagoState.value = Result.failure(e)
            } finally {
                _isLoadingPago.value = false
            }
        }
    }



    // Limpiar estados
    fun limpiarHorarios() {
        _horariosState.value = null
    }

    fun limpiarReservas() {
        _reservasState.value = null
    }

    fun limpiarDetalle() {
        _reservaDetalleState.value = null
    }

    fun limpiarCreacion() {
        _crearReservaState.value = null
    }

    fun limpiarCancelacion() {
        _cancelacionState.value = null
    }

    fun limpiarInicioPago() {
        _inicioPagoState.value = null
    }
}
