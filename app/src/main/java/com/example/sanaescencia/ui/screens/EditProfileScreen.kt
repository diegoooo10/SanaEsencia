
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sanaescencia.data.remote.dto.UsuarioResponse
import com.example.sanaescencia.data.remote.dto.UsuarioUpdateRequest

private val VerdeEditar = Color(0xFF617A56)
private val FondoEditar = Color(0xFFF9F8F6)
private val TextoEditar = Color(0xFF344B35)


@Composable
fun EditProfileScreen(
    usuario: UsuarioResponse?,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    error: String? = null,
    onBack: () -> Unit = {},
    onSave: (UsuarioUpdateRequest) -> Unit = {}
) {

    var correo by remember(usuario) {
        mutableStateOf(usuario?.correo.orEmpty())
    }

    var telefono by remember(usuario) {
        mutableStateOf(usuario?.telefono.orEmpty())
    }

    var direccion by remember(usuario) {
        mutableStateOf(usuario?.direccion.orEmpty())
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FondoEditar)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Encabezado
        Row(
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = VerdeEditar
                )
            }

            Text(
                text = "Editar información",
                style = MaterialTheme.typography.headlineSmall,
                color = TextoEditar,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = "Actualiza tus datos personales.",
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
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                Text(
                    text = "Datos personales",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextoEditar,
                    fontWeight = FontWeight.SemiBold
                )

                // Campos no editables
                ReadOnlyProfileField(
                    value = usuario?.nombre ?: "No disponible",
                    label = "Nombre"
                )

                ReadOnlyProfileField(
                    value = usuario?.apellido ?: "No disponible",
                    label = "Apellido"
                )

                ReadOnlyProfileField(
                    value = usuario?.fechaNacimiento
                        ?.split("-")
                        ?.takeIf { it.size == 3 }
                        ?.let { "${it[2]}-${it[1]}-${it[0]}" }
                        ?: "No disponible",
                    label = "Fecha de nacimiento"
                )

                HorizontalDivider(
                    color = Color(0xFFE4E7DF)
                )

                Text(
                    text = "Información de contacto",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextoEditar,
                    fontWeight = FontWeight.SemiBold
                )

                // Campos editables
                ProfileTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    label = "Correo electrónico"
                )

                ProfileTextField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    label = "Teléfono"
                )

                ProfileTextField(
                    value = direccion,
                    onValueChange = { direccion = it },
                    label = "Dirección (opcional)"
                )
            }
        }

        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // Guardar cambios
        Button(
            onClick = {
                onSave(
                    UsuarioUpdateRequest(
                        correo = correo.trim(),
                        telefono = telefono.trim(),
                        direccion = direccion.trim()
                    )
                )
            },
            enabled = !isLoading && usuario != null,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = VerdeEditar
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
                    text = "Guardar cambios",
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }
        }
    }
}

// Campo de solo lectura
@Composable
private fun ReadOnlyProfileField(
    value: String,
    label: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = label,
            color = Color.Gray,
            style = MaterialTheme.typography.labelMedium
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color(0xFFF0F0ED),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(
                    horizontal = 14.dp,
                    vertical = 16.dp
                )
        ) {
            Text(
                text = value,
                color = Color.Gray,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

// Campo editable
@Composable
private fun ProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = VerdeEditar,
            focusedLabelColor = VerdeEditar,
            cursorColor = VerdeEditar
        )
    )
}
