
package com.example.sanaescencia.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

import com.example.sanaescencia.ui.components.Navbar
import com.example.sanaescencia.ui.screens.LoginScreen
import com.example.sanaescencia.ui.screens.RegisterScreen
import com.example.sanaescencia.ui.screens.HomeScreen
import com.example.sanaescencia.ui.screens.ProfileScreen
import com.example.sanaescencia.ui.screens.EditProfileScreen
import com.example.sanaescencia.ui.screens.ReservationScreen
import com.example.sanaescencia.ui.screens.NewReservationScreen
import com.example.sanaescencia.ui.screens.ReservationDetailScreen
import com.example.sanaescencia.ui.screens.ChangePasswordScreen
import com.example.sanaescencia.ui.screens.NewPasswordScreen

import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sanaescencia.data.remote.RetrofitClient
import com.example.sanaescencia.data.repository.UsuarioRepositoryImpl
import com.example.sanaescencia.ui.viewmodel.UsuarioViewModel
import com.example.sanaescencia.ui.viewmodel.UsuarioViewModelFactory
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import com.example.sanaescencia.data.repository.ProfesionalRepositoryImpl
import com.example.sanaescencia.ui.viewmodel.ProfesionalViewModel
import com.example.sanaescencia.ui.viewmodel.ProfesionalViewModelFactory
import com.example.sanaescencia.data.repository.ServicioRepositoryImpl
import com.example.sanaescencia.ui.viewmodel.ServicioViewModel
import com.example.sanaescencia.ui.viewmodel.ServicioViewModelFactory
import com.example.sanaescencia.data.repository.ReservaRepositoryImpl
import com.example.sanaescencia.ui.viewmodel.ReservaViewModel
import com.example.sanaescencia.ui.viewmodel.ReservaViewModelFactory
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.sanaescencia.data.remote.dto.InicioPagoResponse
import com.example.sanaescencia.ui.screens.WebpayScreen
import com.example.sanaescencia.data.model.Reserva
import com.example.sanaescencia.ui.components.ReservationConfirmationDialog

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.core.tween

private fun obtenerOrdenRuta(route: String?): Int {
    return when (route) {
        Routes.HOME -> 0
        Routes.RESERVATIONS -> 1
        Routes.PROFILE -> 2
        else -> -1
    }
}
@Composable
fun AppNavigation(
    recoveryToken: String? = null
) {

    val navController = rememberNavController()

    var pagoActual by remember {
        mutableStateOf<InicioPagoResponse?>(null)
    }

    var reservaConfirmada by remember {
        mutableStateOf<Reserva?>(null)
    }

    LaunchedEffect(recoveryToken) {
        if (!recoveryToken.isNullOrBlank()) {
            navController.navigate(
                "${Routes.NEW_PASSWORD}?token=${android.net.Uri.encode(recoveryToken)}"
            ) {
                launchSingleTop = true
            }
        }
    }

    val usuarioRepository = remember {
        UsuarioRepositoryImpl(
            RetrofitClient.usuarioApiService
        )
    }

    val usuarioViewModel: UsuarioViewModel = viewModel(
        factory = UsuarioViewModelFactory(usuarioRepository)
    )

    val token by usuarioViewModel.token.collectAsState()


    val profesionalRepository = remember {
        ProfesionalRepositoryImpl(
            RetrofitClient.profesionalApiService
        )
    }

    val profesionalViewModel: ProfesionalViewModel = viewModel(
        factory = ProfesionalViewModelFactory(profesionalRepository)
    )

    val servicioRepository = remember {
        ServicioRepositoryImpl(
            RetrofitClient.servicioApiService
        )
    }

    val servicioViewModel: ServicioViewModel = viewModel(
        factory = ServicioViewModelFactory(servicioRepository)
    )


    val reservaRepository = remember {
        ReservaRepositoryImpl(
            RetrofitClient.reservaApiService
        )
    }

    val reservaViewModel: ReservaViewModel = viewModel(
        factory = ReservaViewModelFactory(reservaRepository)
    )


    val profesionalesState by
    profesionalViewModel.profesionalesState.collectAsState()

    val profesionalesLoading by
    profesionalViewModel.isLoading.collectAsState()

    val especialidadesState by
    servicioViewModel.especialidadesState.collectAsState()

    val serviciosState by
    servicioViewModel.serviciosState.collectAsState()

    val reservasState by
    reservaViewModel.reservasState.collectAsState()

    val cancelacionState by reservaViewModel.cancelacionState.collectAsState()

    val reservas = reservasState?.getOrNull().orEmpty()

    val reservaDetalleState by reservaViewModel.reservaDetalleState.collectAsState()

    val inicioPagoState by reservaViewModel.inicioPagoState.collectAsState()

    LaunchedEffect(token) {
        if (!token.isNullOrBlank()) {
            profesionalViewModel.obtenerProfesionales(token!!)
            servicioViewModel.cargarDatos(token!!)
            reservaViewModel.obtenerReservas(token!!)
        } else {
            profesionalViewModel.limpiarProfesionales()
            servicioViewModel.limpiarEspecialidades()
            servicioViewModel.limpiarServicios()
            reservaViewModel.limpiarReservas()
        }
    }


    val perfilState by usuarioViewModel.perfilState.collectAsState()
    val actualizacionState by usuarioViewModel.actualizacionState.collectAsState()
    val isLoading by usuarioViewModel.isLoading.collectAsState()
    val recuperacionState by usuarioViewModel.recuperacionState.collectAsState()
    val restablecimientoState by usuarioViewModel.restablecimientoState.collectAsState()

    val nombreUsuario = perfilState
        ?.getOrNull()
        ?.nombre
        .orEmpty()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    LaunchedEffect(currentRoute, token) {
        if (currentRoute == Routes.RESERVATIONS && !token.isNullOrBlank()) {
            reservaViewModel.obtenerReservas(token!!)
        }
    }


    LaunchedEffect(inicioPagoState) {
        inicioPagoState?.onSuccess { respuesta ->
            pagoActual = respuesta

            reservaViewModel.limpiarInicioPago()

            navController.navigate(Routes.WEBPAY) {
                launchSingleTop = true
            }
        }
    }


    LaunchedEffect(reservaDetalleState) {
        reservaDetalleState?.onSuccess { reserva ->

            if (
                currentRoute == Routes.WEBPAY &&
                pagoActual?.pago?.reservaId == reserva.id &&
                reserva.estado.equals("CONFIRMADA", ignoreCase = true)
            ) {
                reservaConfirmada = reserva
                reservaViewModel.limpiarDetalle()
            }
        }
    }



    LaunchedEffect(cancelacionState) {
        if (cancelacionState?.isSuccess == true) {
            reservaViewModel.limpiarCancelacion()

            if (!token.isNullOrBlank()) {
                reservaViewModel.obtenerReservas(token!!)
            }
        }
    }

    val mainRoutes = listOf(
        Routes.HOME,
        Routes.RESERVATIONS,
        Routes.PROFILE
    )

    Scaffold(
        bottomBar = {
            if (currentRoute in mainRoutes) {
                Navbar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(Routes.HOME) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Routes.LOGIN,
            modifier = Modifier.padding(innerPadding),

            enterTransition = {
                val initialOrder = obtenerOrdenRuta(initialState.destination.route)
                val targetOrder = obtenerOrdenRuta(targetState.destination.route)

                val haciaIzquierda = initialOrder < targetOrder

                fadeIn(tween(250)) + slideInHorizontally(
                    initialOffsetX = {
                        if (haciaIzquierda) it / 12 else -it / 12
                    },
                    animationSpec = tween(250)
                )
            },

            exitTransition = {
                val initialOrder = obtenerOrdenRuta(initialState.destination.route)
                val targetOrder = obtenerOrdenRuta(targetState.destination.route)

                val haciaIzquierda = initialOrder < targetOrder

                fadeOut(tween(200)) + slideOutHorizontally(
                    targetOffsetX = {
                        if (haciaIzquierda) -it / 12 else it / 12
                    },
                    animationSpec = tween(200)
                )
            },

            popEnterTransition = {
                val initialOrder = obtenerOrdenRuta(initialState.destination.route)
                val targetOrder = obtenerOrdenRuta(targetState.destination.route)

                val haciaIzquierda = initialOrder < targetOrder

                fadeIn(tween(250)) + slideInHorizontally(
                    initialOffsetX = {
                        if (haciaIzquierda) it / 12 else -it / 12
                    },
                    animationSpec = tween(250)
                )
            },

            popExitTransition = {
                val initialOrder = obtenerOrdenRuta(initialState.destination.route)
                val targetOrder = obtenerOrdenRuta(targetState.destination.route)

                val haciaIzquierda = initialOrder < targetOrder

                fadeOut(tween(200)) + slideOutHorizontally(
                    targetOffsetX = {
                        if (haciaIzquierda) -it / 12 else it / 12
                    },
                    animationSpec = tween(200)
                )
            }
        ) {


            // Autenticación
            composable(Routes.LOGIN) {
                LoginScreen(
                    viewModel = usuarioViewModel,
                    onLogin = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.LOGIN) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    },
                    onRegister = {
                        navController.navigate(Routes.REGISTER)
                    },
                    onForgotPassword = {
                        navController.navigate(Routes.CHANGE_PASSWORD)
                    }
                )
            }

            composable(Routes.REGISTER) {
                RegisterScreen(
                    viewModel = usuarioViewModel,
                    onLogin = {
                        usuarioViewModel.limpiarRegistro()
                        navController.popBackStack()
                    }
                )
            }

            // Inicio
            composable(Routes.HOME) {

                LaunchedEffect(Unit) {
                    if (!token.isNullOrBlank()) {
                        reservaViewModel.obtenerReservas(token!!)
                    }
                }

                HomeScreen(
                    nombreUsuario = nombreUsuario,
                    reservas = reservas,
                    onViewReservations = {
                        navController.navigate(Routes.RESERVATIONS) {
                            popUpTo(Routes.HOME) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNewReservation = {
                        navController.navigate(Routes.NEW_RESERVATION)
                    }
                )
            }

            // Reservas
            composable(Routes.RESERVATIONS) {
                ReservationScreen(
                    reservas = reservas,
                    onNewReservation = {
                        navController.navigate(Routes.NEW_RESERVATION)
                    },
                    onReservationDetails = { reservationId ->
                        navController.navigate(
                            "${Routes.RESERVATION_DETAIL}/$reservationId"
                        )
                    }
                )
            }

            composable(Routes.NEW_RESERVATION) {
                NewReservationScreen(
                    profesionales = profesionalesState?.getOrNull().orEmpty(),
                    especialidades = especialidadesState?.getOrNull().orEmpty(),
                    servicios = serviciosState?.getOrNull().orEmpty(),
                    token = token.orEmpty(),
                    reservaViewModel = reservaViewModel,
                    onReservationConfirmed = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.NEW_RESERVATION) {
                                inclusive = true
                            }
                        }
                    }
                )
            }


            composable(Routes.WEBPAY) {
                val pago = pagoActual

                if (pago != null) {
                    WebpayScreen(
                        url = pago.url,
                        token = pago.token,
                        onPagoFinalizado = {
                            if (!token.isNullOrBlank()) {
                                reservaViewModel.limpiarDetalle()

                                reservaViewModel.obtenerReservaPorId(
                                    token = token!!,
                                    id = pago.pago.reservaId
                                )
                            }
                        }
                    )
                }
            }


            composable(
                route = "${Routes.RESERVATION_DETAIL}/{reservationId}",
                arguments = listOf(
                    navArgument("reservationId") {
                        type = NavType.LongType
                    }
                )
            ) { backStackEntry ->

                val reservationId = backStackEntry.arguments
                    ?.getLong("reservationId")

                val reserva = reservas.find {
                    it.id == reservationId
                }

                if (reserva != null) {
                    ReservationDetailScreen(
                        reserva = reserva,
                        onBack = {
                            navController.popBackStack()
                        },
                        onCancel = {
                            reservaViewModel.cancelarReserva(
                                token = token.orEmpty(),
                                id = reserva.id
                            )
                        }
                    )
                }
            }

            // Perfil
            composable(Routes.PROFILE) {
                ProfileScreen(
                    usuario = perfilState?.getOrNull(),
                    onEditProfile = {
                        navController.navigate(Routes.EDIT_PROFILE)
                    },
                    onChangePassword = {
                        navController.navigate(Routes.CHANGE_PASSWORD)
                    },
                    onLogout = {
                        usuarioViewModel.limpiarLogin()

                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.HOME) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }

            // Editar perfil
            composable(Routes.EDIT_PROFILE) {

                LaunchedEffect(actualizacionState) {
                    if (actualizacionState?.isSuccess == true) {
                        usuarioViewModel.limpiarActualizacion()
                        navController.popBackStack()
                    }
                }

                EditProfileScreen(
                    usuario = perfilState?.getOrNull(),
                    isLoading = isLoading,
                    error = actualizacionState?.exceptionOrNull()?.message,
                    onBack = {
                        usuarioViewModel.limpiarActualizacion()
                        navController.popBackStack()
                    },
                    onSave = { request ->
                        usuarioViewModel.actualizarPerfil(request)
                    }
                )
            }

            // Recuperación de contraseña
            composable(Routes.CHANGE_PASSWORD) {

                LaunchedEffect(Unit) {
                    usuarioViewModel.limpiarRecuperacion()
                }

                ChangePasswordScreen(
                    recuperacionState = recuperacionState,
                    isLoading = isLoading,
                    onBack = {
                        usuarioViewModel.limpiarRecuperacion()
                        navController.popBackStack()
                    },
                    onSendRecovery = { correo ->
                        usuarioViewModel.solicitarRecuperacion(correo)
                    }
                )
            }


            composable(
                route = Routes.NEW_PASSWORD_WITH_TOKEN,
                arguments = listOf(
                    navArgument("token") {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->

                val tokenRecuperacion =
                    backStackEntry.arguments?.getString("token").orEmpty()

                LaunchedEffect(Unit) {
                    usuarioViewModel.limpiarRestablecimiento()
                }

                NewPasswordScreen(
                    restablecimientoState = restablecimientoState,
                    isLoading = isLoading,
                    onBack = {
                        usuarioViewModel.limpiarRestablecimiento()
                        navController.popBackStack()
                    },
                    onSave = { nuevaPassword ->
                        usuarioViewModel.restablecerPassword(
                            token = tokenRecuperacion,
                            nuevaPassword = nuevaPassword
                        )
                    }
                )
            }
        }
    }


    reservaConfirmada?.let { reserva ->
        ReservationConfirmationDialog(
            service = reserva.servicio,
            professional = reserva.profesional,
            date = reserva.fecha.toString(),
            hour = reserva.hora.toString(),
            modality = reserva.modalidad,
            onDismiss = {
                // Mantener el diálogo hasta pulsar Aceptar
            },
            onConfirm = {
                reservaConfirmada = null
                pagoActual = null

                navController.navigate(Routes.RESERVATIONS) {
                    popUpTo(Routes.NEW_RESERVATION) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        )
    }

}
