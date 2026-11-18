
package com.example.sanaescencia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

private val VerdeNuevaPassword = Color(0xFF617A56)
private val FondoNuevaPassword = Color(0xFFF9F8F6)
private val TextoNuevaPassword = Color(0xFF344B35)

@Composable
fun NewPasswordScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onSave: (String) -> Unit = {},
    restablecimientoState: Result<Unit>? = null,
    isLoading: Boolean = false
) {

    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var showNewPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FondoNuevaPassword)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        // Encabezado
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = VerdeNuevaPassword
                )
            }

            Text(
                text = "Nueva contraseña",
                style = MaterialTheme.typography.headlineSmall,
                color = TextoNuevaPassword,
                fontWeight = FontWeight.Bold
            )
        }

        // Descripción
        Text(
            text = "Establece una nueva contraseña para tu cuenta.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        // Formulario
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Text(
                    text = "Seguridad de la cuenta",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextoNuevaPassword,
                    fontWeight = FontWeight.SemiBold
                )

                // Nueva contraseña
                NewPasswordField(
                    value = newPassword,
                    onValueChange = {
                        newPassword = it
                        errorMessage = null
                    },

                    label = "Nueva contraseña",
                    visible = showNewPassword,
                    onVisibilityChange = {
                        showNewPassword = !showNewPassword
                    }
                )

                Text(
                    text = "Debe contener al menos 8 caracteres.",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )

                // Confirmar contraseña
                NewPasswordField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        errorMessage = null
                    },
                    label = "Confirmar contraseña",
                    visible = showConfirmPassword,
                    onVisibilityChange = {
                        showConfirmPassword = !showConfirmPassword
                    }
                )

                // Mensaje de error
                // Mensaje de error
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = Color(0xFFA45D50),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // Resultado del restablecimiento
                restablecimientoState?.let { resultado ->
                    Text(
                        text = if (resultado.isSuccess) {
                            "Tu contraseña fue actualizada correctamente."
                        } else {
                            "No fue posible restablecer la contraseña. El enlace puede ser inválido o haber expirado."
                        },
                        color = if (resultado.isSuccess) {
                            VerdeNuevaPassword
                        } else {
                            Color(0xFFA45D50)
                        },
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        // Guardar nueva contraseña
        Button(
            onClick = {

                errorMessage = when {
                    newPassword.isBlank() ->
                        "Ingresa tu nueva contraseña."

                    newPassword.length < 8 ->
                        "La contraseña debe tener al menos 8 caracteres."

                    confirmPassword.isBlank() ->
                        "Confirma tu nueva contraseña."

                    newPassword != confirmPassword ->
                        "Las contraseñas no coinciden."

                    else -> null
                }

                if (errorMessage == null) {
                    onSave(newPassword)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && restablecimientoState?.isSuccess != true,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = VerdeNuevaPassword
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Guardar nueva contraseña",
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun NewPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    visible: Boolean,
    onVisibilityChange: () -> Unit
) {

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        visualTransformation = if (visible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        trailingIcon = {
            TextButton(onClick = onVisibilityChange) {
                Text(
                    text = if (visible) "Ocultar" else "Mostrar",
                    color = VerdeNuevaPassword
                )
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = VerdeNuevaPassword,
            focusedLabelColor = VerdeNuevaPassword,
            cursorColor = VerdeNuevaPassword
        )
    )
}
