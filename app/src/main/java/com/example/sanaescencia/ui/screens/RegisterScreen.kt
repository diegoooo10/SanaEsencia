
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
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.example.sanaescencia.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle
import com.example.sanaescencia.ui.viewmodel.UsuarioViewModel
import com.example.sanaescencia.data.remote.dto.UsuarioRequest

@Composable
fun RegisterScreen(
    viewModel: UsuarioViewModel,
    modifier: Modifier = Modifier,
    onLogin: () -> Unit = {}
) {

    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf(TextFieldValue("")) }
    var ciudad by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmarPassword by remember { mutableStateOf("") }

    var mostrarPassword by remember { mutableStateOf(false) }
    var mostrarConfirmacion by remember { mutableStateOf(false) }
    var validar by remember { mutableStateOf(false) }
    var mostrarDialogoRegistro by remember { mutableStateOf(false) }
    val registroState by viewModel.registroState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(registroState) {
        if (registroState?.isSuccess == true) {

            nombre = ""
            apellido = ""
            telefono = ""
            fechaNacimiento = TextFieldValue("")
            ciudad = ""
            correo = ""
            password = ""
            confirmarPassword = ""

            validar = false

            mostrarDialogoRegistro = true
        }
    }

    val verde = Color(0xFF617A56)
    val fondo = Color(0xFFF9F8F6)

    val fechaValida = try {
        val formato = DateTimeFormatter
            .ofPattern("dd-MM-uuuu")
            .withResolverStyle(ResolverStyle.STRICT)

        val fecha = LocalDate.parse(fechaNacimiento.text, formato)

        !fecha.isAfter(LocalDate.now().minusYears(18))
    } catch (e: Exception) {
        false
    }

    val telefonoValido = Regex(
        """^\d{8}$"""
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
            .imePadding()
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
            onValueChange = { nuevoValor ->
                telefono = nuevoValor.filter { it.isDigit() }.take(8)
            },
            label = { Text("Teléfono") },
            placeholder = { Text("12345678") },
            leadingIcon = {
                Text(
                    text = "+56 9",
                    fontWeight = FontWeight.SemiBold,
                    color = verde,
                    modifier = Modifier.padding(start = 8.dp)
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            isError = validar && !telefonoValido,
            supportingText = {
                if (validar && !telefonoValido) {
                    Text("Ingresa los 8 dígitos de tu teléfono.")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = fechaNacimiento,
            onValueChange = { nuevoValor ->

                val digitos = nuevoValor.text
                    .filter { it.isDigit() }
                    .take(8)

                val fechaFormateada = when {
                    digitos.length >= 4 ->
                        "${digitos.substring(0, 2)}-${digitos.substring(2, 4)}-${digitos.substring(4)}"

                    digitos.length >= 2 ->
                        "${digitos.substring(0, 2)}-${digitos.substring(2)}"

                    else -> digitos
                }

                fun posicionCursor(posicionOriginal: Int): Int {
                    val digitosAntes = nuevoValor.text
                        .take(posicionOriginal)
                        .count { it.isDigit() }
                        .coerceAtMost(digitos.length)

                    val posicion = when {
                        digitosAntes <= 1 -> digitosAntes
                        digitosAntes <= 3 -> digitosAntes + 1
                        else -> digitosAntes + 2
                    }

                    return posicion.coerceAtMost(fechaFormateada.length)
                }

                fechaNacimiento = TextFieldValue(
                    text = fechaFormateada,
                    selection = TextRange(
                        posicionCursor(nuevoValor.selection.start),
                        posicionCursor(nuevoValor.selection.end)
                    )
                )
            },
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
                    val fechaFormateada = LocalDate.parse(
                        fechaNacimiento.text,
                        DateTimeFormatter.ofPattern("dd-MM-uuuu")
                    ).toString()

                    val request = UsuarioRequest(
                        nombre = nombre.trim(),
                        apellido = apellido.trim(),
                        correo = correo.trim(),
                        fechaNacimiento = fechaFormateada,
                        password = password,
                        direccion = ciudad.trim().ifBlank { null },
                        telefono = "+56 9 $telefono"
                    )

                    viewModel.registrar(request)
                }
            },
            enabled = !isLoading,
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

        registroState?.onFailure { error ->
            Text(
                text = error.message ?: "No fue posible crear la cuenta.",
                color = MaterialTheme.colorScheme.error
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

    // Confirmación de registro exitoso
    if (mostrarDialogoRegistro) {
        AlertDialog(
            onDismissRequest = {
                // El usuario debe confirmar para continuar
            },
            title = {
                Text(
                    text = "¡Cuenta creada!",
                    color = Color(0xFF344B35),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("Tu cuenta se creó correctamente.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoRegistro = false
                        onLogin()
                    }
                ) {
                    Text(
                        text = "Aceptar",
                        color = verde
                    )
                }
            },
            containerColor = Color.White
        )
    }
}
