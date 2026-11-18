
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

@Composable
fun AppNavigation(
    recoveryToken: String? = null
) {

    val navController = rememberNavController()

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

    val reservas = reservasState?.getOrNull().orEmpty()

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
            modifier = Modifier.padding(innerPadding)
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
                HomeScreen(
                    nombreUsuario = nombreUsuario,
                    onViewReservations = {
                        navController.navigate(Routes.RESERVATIONS)
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
                    reservaViewModel = reservaViewModel
                )
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
}
