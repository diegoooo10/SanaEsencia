package com.example.sanaescencia.testutil

import com.example.sanaescencia.data.remote.RetrofitClient
import com.example.sanaescencia.data.remote.api.ProfesionalApiService
import com.example.sanaescencia.data.remote.api.ReservaApiService
import com.example.sanaescencia.data.remote.api.ServicioApiService
import com.example.sanaescencia.data.remote.api.UsuarioApiService
import sun.misc.Unsafe

/**
 * Inyecta implementaciones falsas de las APIs en el singleton [RetrofitClient].
 *
 * Las propiedades de un `object` de Kotlin se compilan como campos
 * `static final`; la reflexion normal no permite escribir en ellos desde
 * Java 17. [Unsafe] si lo permite y evita modificar el codigo de produccion.
 * Cada clase de prueba de Robolectric se ejecuta en su propio sandbox, por lo
 * que la inyeccion no contamina otras pruebas.
 */
object RetrofitClientInjection {

    private val unsafe: Unsafe by lazy {
        val campo = Unsafe::class.java.getDeclaredField("theUnsafe")
        campo.isAccessible = true
        campo.get(null) as Unsafe
    }

    fun instalar(
        usuario: UsuarioApiService,
        profesional: ProfesionalApiService,
        servicio: ServicioApiService,
        reserva: ReservaApiService
    ) {
        // Fuerza el inicializador estatico antes de escribir: de lo contrario
        // el bloque <clinit> volveria a crear los servicios reales al primer uso.
        Class.forName(
            RetrofitClient::class.java.name,
            true,
            RetrofitClient::class.java.classLoader
        )

        asignar("usuarioApiService", usuario)
        asignar("profesionalApiService", profesional)
        asignar("servicioApiService", servicio)
        asignar("reservaApiService", reserva)
    }

    private fun asignar(nombre: String, valor: Any) {
        val campo = RetrofitClient::class.java.getDeclaredField(nombre)
        unsafe.putObject(
            unsafe.staticFieldBase(campo),
            unsafe.staticFieldOffset(campo),
            valor
        )
    }
}
