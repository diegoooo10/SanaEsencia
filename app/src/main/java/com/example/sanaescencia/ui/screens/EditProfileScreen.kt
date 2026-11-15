
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

private val VerdeEditar = Color(0xFF617A56)
private val FondoEditar = Color(0xFFF9F8F6)
private val TextoEditar = Color(0xFF344B35)

@Composable
fun EditProfileScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onSave: () -> Unit = {}
) {

    var correo by remember { mutableStateOf("maria@correo.cl") }
    var telefono by remember { mutableStateOf("+56 9 1234 5678") }
    var ciudad by remember { mutableStateOf("Villa Alemana") }

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
                    value = "María",
                    label = "Nombre"
                )

                ReadOnlyProfileField(
                    value = "González",
                    label = "Apellido"
                )

                ReadOnlyProfileField(
                    value = "01-01-1995",
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
                    value = ciudad,
                    onValueChange = { ciudad = it },
                    label = "Ciudad (opcional)"
                )
            }
        }

        // Guardar cambios
        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = VerdeEditar
            )
        ) {
            Text(
                text = "Guardar cambios",
                modifier = Modifier.padding(vertical = 6.dp)
            )
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
