
package com.example.sanaescencia.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
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
import com.example.sanaescencia.ui.viewmodel.UsuarioViewModel

@Composable
fun LoginScreen(
    viewModel: UsuarioViewModel,
    modifier: Modifier = Modifier,
    onLogin: () -> Unit = {},
    onRegister: () -> Unit = {},
    onForgotPassword: () -> Unit = {}
) {

    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var mostrarPassword by remember { mutableStateOf(false) }

    val verde = Color(0xFF617A56)

    val loginState by viewModel.loginState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(loginState) {
        loginState?.onSuccess {
            onLogin()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF9F8F6))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(64.dp))

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

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "¡Bienvenido!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF344B35)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Ingresa a tu cuenta para continuar.",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Correo electrónico
        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo electrónico") },
            placeholder = { Text("correo@ejemplo.com") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = verde,
                focusedLabelColor = verde,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Contraseña
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        mostrarPassword = !mostrarPassword
                    }
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
            visualTransformation = if (mostrarPassword)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = verde,
                focusedLabelColor = verde,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Recuperar contraseña
        TextButton(
            onClick = onForgotPassword,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(
                text = "¿Olvidaste tu contraseña?",
                color = verde,
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botones
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Button(
                onClick = {
                    viewModel.iniciarSesion(correo, password)
                },
                enabled = correo.isNotBlank() &&
                        password.isNotBlank() &&
                        !isLoading,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = verde
                ),
                contentPadding = PaddingValues(
                    horizontal = 4.dp,
                    vertical = 12.dp
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Iniciar sesión", maxLines = 1)
                }
            }

            OutlinedButton(
                onClick = onRegister,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    verde
                ),
                contentPadding = PaddingValues(
                    horizontal = 4.dp,
                    vertical = 12.dp
                )
            ) {
                Text(
                    text = "Registrarse",
                    color = verde,
                    maxLines = 1
                )
            }
        }

        loginState?.onFailure { error ->
            Spacer(modifier = Modifier.height(12.dp))

            val mensaje = when (error) {
                is retrofit2.HttpException -> when (error.code()) {
                    400, 401 -> "El correo o la contraseña son incorrectos."
                    403 -> "No tienes autorización para acceder."
                    in 500..599 -> "No pudimos iniciar sesión en este momento. Inténtalo más tarde."
                    else -> "No pudimos iniciar sesión. Inténtalo nuevamente."
                }

                is java.io.IOException ->
                    "No pudimos conectarnos. Revisa tu conexión a internet."

                else ->
                    "No pudimos iniciar sesión en este momento. Inténtalo nuevamente."
            }

            Text(
                text = mensaje,
                color = MaterialTheme.colorScheme.error,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
