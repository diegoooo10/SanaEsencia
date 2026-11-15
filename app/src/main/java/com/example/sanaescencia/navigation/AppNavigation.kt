
package com.example.sanaescencia.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

import com.example.sanaescencia.ui.screens.LoginScreen
import com.example.sanaescencia.ui.screens.RegisterScreen
import com.example.sanaescencia.ui.screens.HomeScreen
import com.example.sanaescencia.ui.screens.ShopScreen
import com.example.sanaescencia.ui.screens.CategoryProductsScreen
import com.example.sanaescencia.ui.screens.ProductDetailScreen
import com.example.sanaescencia.ui.screens.ProfileScreen
import com.example.sanaescencia.ui.screens.EditProfileScreen
import com.example.sanaescencia.ui.screens.ReservationScreen
import com.example.sanaescencia.ui.screens.NewReservationScreen
import com.example.sanaescencia.ui.screens.ReservationDetailScreen
import com.example.sanaescencia.ui.screens.ChangePasswordScreen
import com.example.sanaescencia.ui.screens.NewPasswordScreen
import androidx.compose.ui.Modifier
import androidx.compose.material3.Scaffold
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.getValue
import com.example.sanaescencia.ui.components.Navbar

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val mainRoutes = listOf(
        Routes.HOME,
        Routes.SHOP,
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
                    onLogin = {
                        navController.popBackStack()
                    }
                )
            }

            // Inicio
            composable(Routes.HOME) {
                HomeScreen(
                    onViewReservations = {
                        navController.navigate(Routes.RESERVATIONS)
                    },
                    onNewReservation = {
                        navController.navigate(Routes.NEW_RESERVATION)
                    }
                )
            }

            // Tienda
            composable(Routes.SHOP) {
                ShopScreen(
                    onCategorySelected = { categoryId ->
                        navController.navigate(
                            "${Routes.CATEGORY_PRODUCTS}/$categoryId"
                        )
                    }
                )
            }

            composable(
                route = "${Routes.CATEGORY_PRODUCTS}/{categoryId}",
                arguments = listOf(
                    navArgument("categoryId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->

                val categoryId = backStackEntry.arguments
                    ?.getInt("categoryId") ?: 0

                CategoryProductsScreen(
                    categoryId = categoryId,
                    onBack = {
                        navController.popBackStack()
                    },
                    onProductSelected = { productId ->
                        navController.navigate(
                            "${Routes.PRODUCT_DETAIL}/$productId"
                        )
                    }
                )
            }

            composable(
                route = "${Routes.PRODUCT_DETAIL}/{productId}",
                arguments = listOf(
                    navArgument("productId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->

                val productId = backStackEntry.arguments
                    ?.getInt("productId") ?: 0

                ProductDetailScreen(
                    productId = productId,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            // Reservas
            composable(Routes.RESERVATIONS) {
                ReservationScreen(
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

            // Nueva Reserva
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
            ) {
                ReservationDetailScreen(
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            // Perfil
            composable(Routes.PROFILE) {
                ProfileScreen(
                    onEditProfile = {
                        navController.navigate(Routes.EDIT_PROFILE)
                    },
                    onChangePassword = {
                        navController.navigate(Routes.CHANGE_PASSWORD)
                    },
                    onLogout = {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.HOME) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(Routes.EDIT_PROFILE) {
                EditProfileScreen(
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            // Recuperación de contraseña
            composable(Routes.CHANGE_PASSWORD) {
                ChangePasswordScreen(
                    onBack = {
                        navController.popBackStack()
                    },
                    onSendRecovery = {
                        // Pendiente: conectar recuperación con backend
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
