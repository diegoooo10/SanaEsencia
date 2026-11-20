
package com.example.sanaescencia.navigation

object Routes {

    // Autenticación
    const val LOGIN = "login"
    const val REGISTER = "register"

    // Inicio
    const val HOME = "home"

    // Reservas
    const val RESERVATIONS = "reservations"
    const val NEW_RESERVATION = "new_reservation"
    const val RESERVATION_DETAIL = "reservation_detail"

    const val WEBPAY = "webpay"

    // Perfil
    const val PROFILE = "profile"
    const val EDIT_PROFILE = "edit_profile"

    // Recuperación de contraseña
    const val CHANGE_PASSWORD = "change_password"
    const val NEW_PASSWORD = "new_password"
    const val NEW_PASSWORD_WITH_TOKEN = "new_password?token={token}"
}
