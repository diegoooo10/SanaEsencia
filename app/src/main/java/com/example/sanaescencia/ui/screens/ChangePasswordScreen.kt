
package com.example.sanaescencia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp

private val VerdePassword = Color(0xFF617A56)
private val FondoPassword = Color(0xFFF9F8F6)
private val TextoPassword = Color(0xFF344B35)


@Composable
fun ChangePasswordScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onSendRecovery: (String) -> Unit = {},
    recuperacionState: Result<Unit>? = null,
    isLoading: Boolean = false
)
 {

    var correo by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FondoPassword)
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
                    tint = VerdePassword
                )
            }

            Text(
                text = "Recuperar contraseña",
                style = MaterialTheme.typography.headlineSmall,
                color = TextoPassword,
                fontWeight = FontWeight.Bold
            )
        }

        // Descripción
        Text(
            text = "Ingresa el correo electrónico asociado a tu cuenta. Te enviaremos un enlace para recuperar tu contraseña.",
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
                    text = "Correo electrónico",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextoPassword,
                    fontWeight = FontWeight.SemiBold
                )

                OutlinedTextField(
                    value = correo,
                    onValueChange = {
                        correo = it
                        errorMessage = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text("nombre@correo.cl")
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = VerdePassword
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VerdePassword,
                        focusedLabelColor = VerdePassword,
                        cursorColor = VerdePassword
                    )
                )


                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = Color(0xFFA45D50),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // Resultado de la solicitud de recuperación
                recuperacionState?.let { resultado ->
                    Text(
                        text = if (resultado.isSuccess) {
                            "Si el correo está registrado, recibirás un enlace de recuperación."
                        } else {
                            "No fue posible enviar la solicitud. Inténtalo nuevamente."
                        },
                        color = if (resultado.isSuccess) {
                            VerdePassword
                        } else {
                            Color(0xFFA45D50)
                        },
                        style = MaterialTheme.typography.bodySmall
                    )
                }

            }
        }

        // Enviar enlace
        Button(
            onClick = {

                errorMessage = when {
                    correo.isBlank() ->
                        "Ingresa tu correo electrónico."

                    !android.util.Patterns.EMAIL_ADDRESS
                        .matcher(correo.trim())
                        .matches() ->
                        "Ingresa un correo electrónico válido."

                    else -> null
                }

                if (errorMessage == null) {
                    onSendRecovery(correo.trim())
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = VerdePassword
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
                    text = "Enviar enlace de recuperación",
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }

        }
    }
}
