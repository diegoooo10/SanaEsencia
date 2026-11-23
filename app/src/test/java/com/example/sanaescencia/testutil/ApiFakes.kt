package com.example.sanaescencia.testutil

import com.example.sanaescencia.data.model.Especialidad
import com.example.sanaescencia.data.model.Profesional
import com.example.sanaescencia.data.model.Reserva
import com.example.sanaescencia.data.model.Servicio
import com.example.sanaescencia.data.remote.api.ProfesionalApiService
import com.example.sanaescencia.data.remote.api.ReservaApiService
import com.example.sanaescencia.data.remote.api.ServicioApiService
import com.example.sanaescencia.data.remote.api.UsuarioApiService
import com.example.sanaescencia.data.remote.dto.InicioPagoResponse
import com.example.sanaescencia.data.remote.dto.LoginRequest
import com.example.sanaescencia.data.remote.dto.LoginResponse
import com.example.sanaescencia.data.remote.dto.RecuperacionPasswordRequest
import com.example.sanaescencia.data.remote.dto.ReservaRequest
import com.example.sanaescencia.data.remote.dto.RestablecerPasswordRequest
import com.example.sanaescencia.data.remote.dto.UsuarioRequest
import com.example.sanaescencia.data.remote.dto.UsuarioResponse
import com.example.sanaescencia.data.remote.dto.UsuarioUpdateRequest
import okhttp3.ResponseBody
import retrofit2.Response

/**
 * Implementaciones manuales de las APIs Retrofit. Permiten controlar la
 * respuesta exacta de cada endpoint y capturar los argumentos enviados,
 * sin realizar ninguna peticion de red.
 */
class FakeProfesionalApi : ProfesionalApiService {

    var response: Response<List<Profesional>> = Response.success(listOf(TestData.profesional))
    var error: Throwable? = null
    var lastToken: String? = null

    override suspend fun obtenerProfesionales(token: String): Response<List<Profesional>> {
        lastToken = token
        error?.let { throw it }
        return response
    }
}

class FakeServicioApi : ServicioApiService {

    var especialidadesResponse: Response<List<Especialidad>> =
        Response.success(listOf(TestData.especialidad))
    var serviciosResponse: Response<List<Servicio>> =
        Response.success(listOf(TestData.servicio))
    var error: Throwable? = null

    var lastEspecialidadesToken: String? = null
    var lastServiciosToken: String? = null
    var lastEspecialidadId: Int? = null
    var serviciosCalled = false

    override suspend fun obtenerEspecialidades(token: String): Response<List<Especialidad>> {
        lastEspecialidadesToken = token
        error?.let { throw it }
        return especialidadesResponse
    }

    override suspend fun obtenerServicios(
        token: String,
        especialidadId: Int?
    ): Response<List<Servicio>> {
        serviciosCalled = true
        lastServiciosToken = token
        lastEspecialidadId = especialidadId
        error?.let { throw it }
        return serviciosResponse
    }
}

class FakeReservaApi : ReservaApiService {

    var horariosResponse: Response<List<String>> = Response.success(listOf("09:00", "10:30"))
    var crearResponse: Response<Reserva> = Response.success(TestData.reserva)
    var reservasResponse: Response<List<Reserva>> = Response.success(listOf(TestData.reserva))
    var detalleResponse: Response<Reserva> = Response.success(TestData.reserva)
    var cancelarResponse: Response<Reserva> = Response.success(TestData.reserva)
    var pagoResponse: Response<InicioPagoResponse> = Response.success(TestData.inicioPago)
    var error: Throwable? = null

    var lastHorariosToken: String? = null
    var lastHorariosProfesionalId: Long? = null
    var lastHorariosServicioId: Long? = null
    var lastHorariosFecha: String? = null
    var lastHorariosModalidad: String? = null

    var lastCrearToken: String? = null
    var lastCrearRequest: ReservaRequest? = null
    var lastReservasToken: String? = null
    var lastDetalleToken: String? = null
    var lastDetalleId: Long? = null
    var lastCancelarToken: String? = null
    var lastCancelarId: Long? = null
    var lastPagoToken: String? = null
    var lastPagoId: Long? = null

    override suspend fun obtenerHorariosDisponibles(
        token: String,
        profesionalId: Long,
        servicioId: Long,
        fecha: String,
        modalidad: String
    ): Response<List<String>> {
        lastHorariosToken = token
        lastHorariosProfesionalId = profesionalId
        lastHorariosServicioId = servicioId
        lastHorariosFecha = fecha
        lastHorariosModalidad = modalidad
        error?.let { throw it }
        return horariosResponse
    }

    override suspend fun crearReserva(
        token: String,
        reserva: ReservaRequest
    ): Response<Reserva> {
        lastCrearToken = token
        lastCrearRequest = reserva
        error?.let { throw it }
        return crearResponse
    }

    override suspend fun obtenerReservas(token: String): Response<List<Reserva>> {
        lastReservasToken = token
        error?.let { throw it }
        return reservasResponse
    }

    override suspend fun obtenerReservaPorId(token: String, id: Long): Response<Reserva> {
        lastDetalleToken = token
        lastDetalleId = id
        error?.let { throw it }
        return detalleResponse
    }

    override suspend fun cancelarReserva(token: String, id: Long): Response<Reserva> {
        lastCancelarToken = token
        lastCancelarId = id
        error?.let { throw it }
        return cancelarResponse
    }

    override suspend fun iniciarPago(token: String, id: Long): Response<InicioPagoResponse> {
        lastPagoToken = token
        lastPagoId = id
        error?.let { throw it }
        return pagoResponse
    }
}

class FakeUsuarioApi : UsuarioApiService {

    var registrarResponse: Response<UsuarioResponse> = Response.success(TestData.usuarioResponse)
    var loginResponse: Response<LoginResponse> = Response.success(TestData.loginResponse)
    var perfilResponse: Response<UsuarioResponse> = Response.success(TestData.usuarioResponse)
    var actualizarResponse: Response<UsuarioResponse> = Response.success(TestData.usuarioResponse)
    var recuperacionResponse: Response<ResponseBody> =
        Response.success(okhttp3.ResponseBody.create(null, "{}"))
    var restablecerResponse: Response<ResponseBody> =
        Response.success(okhttp3.ResponseBody.create(null, "{}"))
    var error: Throwable? = null

    var lastRegistrarRequest: UsuarioRequest? = null
    var lastLoginRequest: LoginRequest? = null
    var lastPerfilToken: String? = null
    var lastActualizarToken: String? = null
    var lastActualizarRequest: UsuarioUpdateRequest? = null
    var lastRecuperacionRequest: RecuperacionPasswordRequest? = null
    var lastRestablecerRequest: RestablecerPasswordRequest? = null

    override suspend fun registrar(request: UsuarioRequest): Response<UsuarioResponse> {
        lastRegistrarRequest = request
        error?.let { throw it }
        return registrarResponse
    }

    override suspend fun iniciarSesion(request: LoginRequest): Response<LoginResponse> {
        lastLoginRequest = request
        error?.let { throw it }
        return loginResponse
    }

    override suspend fun solicitarRecuperacion(
        request: RecuperacionPasswordRequest
    ): Response<ResponseBody> {
        lastRecuperacionRequest = request
        error?.let { throw it }
        return recuperacionResponse
    }

    override suspend fun obtenerPerfil(token: String): Response<UsuarioResponse> {
        lastPerfilToken = token
        error?.let { throw it }
        return perfilResponse
    }

    override suspend fun actualizarPerfil(
        token: String,
        request: UsuarioUpdateRequest
    ): Response<UsuarioResponse> {
        lastActualizarToken = token
        lastActualizarRequest = request
        error?.let { throw it }
        return actualizarResponse
    }

    override suspend fun restablecerPassword(
        request: RestablecerPasswordRequest
    ): Response<ResponseBody> {
        lastRestablecerRequest = request
        error?.let { throw it }
        return restablecerResponse
    }
}
