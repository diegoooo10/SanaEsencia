
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
import com.example.sanaescencia.data.model.Reserva
import java.time.LocalDate
import java.time.LocalTime

import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sanaescencia.data.remote.RetrofitClient
import com.example.sanaescencia.data.repository.UsuarioRepositoryImpl
import com.example.sanaescencia.ui.viewmodel.UsuarioViewModel
import com.example.sanaescencia.ui.viewmodel.UsuarioViewModelFactory
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    val usuarioRepository = remember {
        UsuarioRepositoryImpl(
            RetrofitClient.usuarioApiService
        )
    }

    val usuarioViewModel: UsuarioViewModel = viewModel(
        factory = UsuarioViewModelFactory(usuarioRepository)
    )

    val perfilState by usuarioViewModel.perfilState.collectAsState()
    val actualizacionState by usuarioViewModel.actualizacionState.collectAsState()
    val isLoading by usuarioViewModel.isLoading.collectAsState()
    val recuperacionState by usuarioViewModel.recuperacionState.collectAsState()

    val nombreUsuario = perfilState
        ?.getOrNull()
        ?.nombre
        .orEmpty()

    val reservasDemo = remember {
        listOf(
            Reserva(
                id = 1,
                especialidad = "Masoterapia",
                servicio = "Masaje de relajación",
                profesional = "Profesional A",
                fecha = LocalDate.of(2026, 11, 17),
                hora = LocalTime.of(16, 0),
                modalidad = "Presencial",
                estado = "Confirmada",
                duracionMinutos = 60
            )
        )
    }

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
                    reservas = reservasDemo,
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
                NewReservationScreen()
            }

            composable(
                route = "${Routes.RESERVATION_DETAIL}/{reservationId}",
                arguments = listOf(
                    navArgument("reservationId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->

                val reservationId = backStackEntry.arguments
                    ?.getInt("reservationId")

                val reserva = reservasDemo.find {
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


            composable(Routes.NEW_PASSWORD) {
                NewPasswordScreen(
                    onBack = {
                        navController.popBackStack()
                    },
                    onSave = {
                        // Pendiente: conectar actualización con backend
                    }
                )
            }
        }
    }
}
