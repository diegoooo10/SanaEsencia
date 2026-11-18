
package com.example.sanaescencia.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ReservationConfirmationDialog(
    service: String,
    professional: String,
    date: String,
    hour: String,
    modality: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "¡Reserva confirmada!",
                color = Color(0xFF344B35),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Tu reserva ha sido registrada correctamente.")

                Text("Servicio: $service")
                Text("Profesional: $professional")
                Text("Fecha: $date")
                Text("Hora: $hour")
                Text("Modalidad: $modality")
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = "Aceptar",
                    color = Color(0xFF617A56)
                )
            }
        },
        containerColor = Color.White
    )
}
