package com.example.sanaescencia.testutil

import com.example.sanaescencia.data.model.Especialidad
import com.example.sanaescencia.data.model.Profesional
import com.example.sanaescencia.data.model.Reserva
import com.example.sanaescencia.data.model.Servicio
import com.example.sanaescencia.data.remote.dto.InicioPagoResponse
import com.example.sanaescencia.data.remote.dto.LoginRequest
import com.example.sanaescencia.data.remote.dto.LoginResponse
import com.example.sanaescencia.data.remote.dto.RecuperacionPasswordRequest
import com.example.sanaescencia.data.remote.dto.ReservaRequest
import com.example.sanaescencia.data.remote.dto.RestablecerPasswordRequest
import com.example.sanaescencia.data.remote.dto.UsuarioRequest
import com.example.sanaescencia.data.remote.dto.UsuarioResponse
import com.example.sanaescencia.data.remote.dto.UsuarioUpdateRequest
import com.example.sanaescencia.data.repository.ProfesionalRepository
import com.example.sanaescencia.data.repository.ReservaRepository
import com.example.sanaescencia.data.repository.ServicioRepository
import com.example.sanaescencia.data.repository.UsuarioRepository
import kotlinx.coroutines.CompletableDeferred
import java.time.LocalDate
import java.time.LocalTime

/**
 * Fakes manuales: permiten controlar resultados, capturar argumentos y simular
 * suspensión para verificar estados de carga y concurrencia.
 */
class FakeProfesionalRepository : ProfesionalRepository {

    var result: Result<List<Profesional>> = Result.success(listOf(TestData.profesional))
    var throwable: Throwable? = null
    var gate: CompletableDeferred<Unit>? = null
    var calls = 0
    var lastToken: String? = null

    override suspend fun obtenerProfesionales(token: String): Result<List<Profesional>> {
        calls++
        lastToken = token
        gate?.await()
        throwable?.let { throw it }
        return result
    }
}

class FakeServicioRepository : ServicioRepository {

    var especialidadesResult: Result<List<Especialidad>> =
        Result.success(listOf(TestData.especialidad))
    var serviciosResult: Result<List<Servicio>> = Result.success(listOf(TestData.servicio))
    var throwable: Throwable? = null
    var gate: CompletableDeferred<Unit>? = null
    var especialidadesCalls = 0
    var serviciosCalls = 0
    var lastEspecialidadesToken: String? = null
    var lastServiciosToken: String? = null
    var lastEspecialidadId: Int? = null
    var serviciosFilterWasNull = false

    override suspend fun obtenerEspecialidades(token: String): Result<List<Especialidad>> {
        especialidadesCalls++
        lastEspecialidadesToken = token
        gate?.await()
        throwable?.let { throw it }
        return especialidadesResult
    }

    override suspend fun obtenerServicios(
        token: String,
        especialidadId: Int?
    ): Result<List<Servicio>> {
        serviciosCalls++
        lastServiciosToken = token
        lastEspecialidadId = especialidadId
        serviciosFilterWasNull = especialidadId == null
        gate?.await()
        throwable?.let { throw it }
        return serviciosResult
    }
}

class FakeReservaRepository : ReservaRepository {

    var horariosResult: Result<List<LocalTime>> =
        Result.success(listOf(LocalTime.of(9, 0), LocalTime.of(10, 30)))
    var reservasResult: Result<List<Reserva>> = Result.success(listOf(TestData.reserva))
    var detalleResult: Result<Reserva> = Result.success(TestData.reserva)
    var crearResult: Result<Reserva> = Result.success(TestData.reserva)
    var cancelarResult: Result<Unit> = Result.success(Unit)
    var pagoResult: Result<InicioPagoResponse> = Result.success(TestData.inicioPago)

    var throwable: Throwable? = null
    var gate: CompletableDeferred<Unit>? = null

    var horariosCalls = 0
    var reservasCalls = 0
    var detalleCalls = 0
    var crearCalls = 0
    var cancelarCalls = 0
    var pagoCalls = 0

    var lastHorariosToken: String? = null
    var lastProfesionalId: Long? = null
    var lastServicioId: Long? = null
    var lastFecha: LocalDate? = null
    var lastModalidad: String? = null

    var lastReservasToken: String? = null
    var lastDetalleToken: String? = null
    var lastDetalleId: Long? = null
    var lastCrearToken: String? = null
    var lastCrearRequest: ReservaRequest? = null
    var lastCancelarToken: String? = null
    var lastCancelarId: Long? = null
    var lastPagoToken: String? = null
    var lastPagoReservaId: Long? = null

    override suspend fun obtenerHorariosDisponibles(
        token: String,
        profesionalId: Long,
        servicioId: Long,
        fecha: LocalDate,
        modalidad: String
    ): Result<List<LocalTime>> {
        horariosCalls++
        lastHorariosToken = token
        lastProfesionalId = profesionalId
        lastServicioId = servicioId
        lastFecha = fecha
        lastModalidad = modalidad
        gate?.await()
        throwable?.let { throw it }
        return horariosResult
    }

    override suspend fun obtenerReservas(token: String): Result<List<Reserva>> {
        reservasCalls++
        lastReservasToken = token
        gate?.await()
        throwable?.let { throw it }
        return reservasResult
    }

    override suspend fun obtenerReservaPorId(token: String, id: Long): Result<Reserva> {
        detalleCalls++
        lastDetalleToken = token
        lastDetalleId = id
        gate?.await()
        throwable?.let { throw it }
        return detalleResult
    }

    override suspend fun crearReserva(
        token: String,
        request: ReservaRequest
    ): Result<Reserva> {
        crearCalls++
        lastCrearToken = token
        lastCrearRequest = request
        gate?.await()
        throwable?.let { throw it }
        return crearResult
    }

    override suspend fun cancelarReserva(token: String, id: Long): Result<Unit> {
        cancelarCalls++
        lastCancelarToken = token
        lastCancelarId = id
        gate?.await()
        throwable?.let { throw it }
        return cancelarResult
    }

    override suspend fun iniciarPago(token: String, reservaId: Long): Result<InicioPagoResponse> {
        pagoCalls++
        lastPagoToken = token
        lastPagoReservaId = reservaId
        gate?.await()
        throwable?.let { throw it }
        return pagoResult
    }
}

class FakeUsuarioRepository : UsuarioRepository {

    var registrarResult: Result<UsuarioResponse> = Result.success(TestData.usuarioResponse)
    var iniciarSesionResult: Result<LoginResponse> = Result.success(TestData.loginResponse)
    var perfilResult: Result<UsuarioResponse> = Result.success(TestData.usuarioResponse)
    var actualizarResult: Result<UsuarioResponse> = Result.success(TestData.usuarioResponse)
    var recuperacionResult: Result<Unit> = Result.success(Unit)
    var restablecerResult: Result<Unit> = Result.success(Unit)

    var throwable: Throwable? = null
    var gate: CompletableDeferred<Unit>? = null

    var registrarCalls = 0
    var loginCalls = 0
    var perfilCalls = 0
    var actualizarCalls = 0
    var recuperacionCalls = 0
    var restablecerCalls = 0

    var lastRegistrarRequest: UsuarioRequest? = null
    var lastLoginRequest: LoginRequest? = null
    var lastPerfilToken: String? = null
    var lastActualizarToken: String? = null
    var lastActualizarRequest: UsuarioUpdateRequest? = null
    var lastRecuperacionRequest: RecuperacionPasswordRequest? = null
    var lastRestablecerRequest: RestablecerPasswordRequest? = null

    override suspend fun registrar(request: UsuarioRequest): Result<UsuarioResponse> {
        registrarCalls++
        lastRegistrarRequest = request
        gate?.await()
        throwable?.let { throw it }
        return registrarResult
    }

    override suspend fun iniciarSesion(request: LoginRequest): Result<LoginResponse> {
        loginCalls++
        lastLoginRequest = request
        gate?.await()
        throwable?.let { throw it }
        return iniciarSesionResult
    }

    override suspend fun obtenerPerfil(token: String): Result<UsuarioResponse> {
        perfilCalls++
        lastPerfilToken = token
        gate?.await()
        throwable?.let { throw it }
        return perfilResult
    }

    override suspend fun actualizarPerfil(
        token: String,
        request: UsuarioUpdateRequest
    ): Result<UsuarioResponse> {
        actualizarCalls++
        lastActualizarToken = token
        lastActualizarRequest = request
        gate?.await()
        throwable?.let { throw it }
        return actualizarResult
    }

    override suspend fun solicitarRecuperacion(
        request: RecuperacionPasswordRequest
    ): Result<Unit> {
        recuperacionCalls++
        lastRecuperacionRequest = request
        gate?.await()
        throwable?.let { throw it }
        return recuperacionResult
    }

    override suspend fun restablecerPassword(
        request: RestablecerPasswordRequest
    ): Result<Unit> {
        restablecerCalls++
        lastRestablecerRequest = request
        gate?.await()
        throwable?.let { throw it }
        return restablecerResult
    }
}
