
package com.example.sanaescencia.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sanaescencia.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle
import java.time.temporal.ChronoUnit

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    onLogin: () -> Unit = {}
) {

    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("+56 9 ") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var ciudad by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmarPassword by remember { mutableStateOf("") }

    var mostrarPassword by remember { mutableStateOf(false) }
    var mostrarConfirmacion by remember { mutableStateOf(false) }
    var validar by remember { mutableStateOf(false) }

    val verde = Color(0xFF617A56)
    val fondo = Color(0xFFF9F8F6)

    val fechaValida = try {
        val formato = DateTimeFormatter
            .ofPattern("dd-MM-uuuu")
            .withResolverStyle(ResolverStyle.STRICT)

        val fecha = LocalDate.parse(fechaNacimiento, formato)

        !fecha.isAfter(LocalDate.now().minusYears(18))
    } catch (e: Exception) {
        false
    }

    val telefonoValido = Regex(
        """^\+56 9 \d{8}$"""
    ).matches(telefono)

    val correoValido = Regex(
        """^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$"""
    ).matches(correo)

    val formularioValido =
        nombre.isNotBlank() &&
                apellido.isNotBlank() &&
                telefonoValido &&
                fechaValida &&
                correoValido &&
                password.length >= 8 &&
                password == confirmarPassword

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(fondo)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(40.dp))

        // Logo unificado
        Image(
            painter = painterResource(R.drawable.logo_header),
            contentDescription = "Logo Sanaesencia",
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .offset(x = (-10).dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Crear cuenta",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF344B35)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Completa tus datos para registrarte.",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // DATOS PERSONALES
        Text(
            text = "Datos personales",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = verde,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                singleLine = true,
                isError = validar && nombre.isBlank(),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = apellido,
                onValueChange = { apellido = it },
                label = { Text("Apellido") },
                singleLine = true,
                isError = validar && apellido.isBlank(),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            )
        }

        if (validar && (nombre.isBlank() || apellido.isBlank())) {
            Text(
                text = "Ingresa tu nombre y apellido.",
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = telefono,
            onValueChange = { telefono = it },
            label = { Text("Teléfono") },
            placeholder = { Text("+56 9 XXXXXXXX") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone
            ),
            isError = validar && !telefonoValido,
            supportingText = {
                if (validar && !telefonoValido) {
                    Text("Formato: +56 9 seguido de 8 dígitos.")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = fechaNacimiento,
            onValueChange = { fechaNacimiento = it },
            label = { Text("Fecha de nacimiento") },
            placeholder = { Text("DD-MM-AAAA") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            isError = validar && !fechaValida,
            supportingText = {
                Text(
                    if (validar && !fechaValida)
                        "Ingresa una fecha válida. Debes tener al menos 18 años."
                    else
                        "Debes tener al menos 18 años."
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = ciudad,
            onValueChange = { ciudad = it },
            label = { Text("Ciudad (opcional)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        HorizontalDivider(color = Color(0xFFE4E7DF))

        Spacer(modifier = Modifier.height(24.dp))

        // DATOS DE ACCESO
        Text(
            text = "Datos de acceso",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = verde,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo electrónico") },
            placeholder = { Text("nombre@correo.dominio") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            isError = validar && !correoValido,
            supportingText = {
                if (validar && !correoValido) {
                    Text("Ingresa un correo electrónico válido.")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = if (mostrarPassword)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(
                    onClick = { mostrarPassword = !mostrarPassword }
                ) {
                    Icon(
                        imageVector = if (mostrarPassword)
                            Icons.Default.VisibilityOff
                        else
                            Icons.Default.Visibility,
                        contentDescription = if (mostrarPassword)
                            "Ocultar contraseña"
                        else
                            "Mostrar contraseña"
                    )
                }
            },
            isError = validar && password.length < 8,
            supportingText = {
                Text(
                    if (validar && password.length < 8)
                        "La contraseña debe tener al menos 8 caracteres."
                    else
                        "Mínimo 8 caracteres."
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = confirmarPassword,
            onValueChange = { confirmarPassword = it },
            label = { Text("Confirmar contraseña") },
            singleLine = true,
            visualTransformation = if (mostrarConfirmacion)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(
                    onClick = {
                        mostrarConfirmacion = !mostrarConfirmacion
                    }
                ) {
                    Icon(
                        imageVector = if (mostrarConfirmacion)
                            Icons.Default.VisibilityOff
                        else
                            Icons.Default.Visibility,
                        contentDescription = if (mostrarConfirmacion)
                            "Ocultar contraseña"
                        else
                            "Mostrar contraseña"
                    )
                }
            },
            isError = validar && password != confirmarPassword,
            supportingText = {
                if (validar && password != confirmarPassword) {
                    Text("Las contraseñas no coinciden.")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // CREAR CUENTA
        Button(
            onClick = {
                validar = true

                if (formularioValido) {
                    // Pendiente: conectar con el microservicio.
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = verde
            )
        ) {
            Text(
                text = "Crear cuenta",
                modifier = Modifier.padding(vertical = 6.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "¿Ya tienes una cuenta?",
            color = Color.Gray,
            fontSize = 13.sp
        )

        TextButton(onClick = onLogin) {
            Text(
                text = "Iniciar sesión",
                color = verde
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
