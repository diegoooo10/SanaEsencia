
package com.example.sanaescencia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sanaescencia.data.model.Reserva
import java.time.format.DateTimeFormatter
import java.util.Locale

private val VerdeDetalle = Color(0xFF617A56)
private val FondoDetalle = Color(0xFFF9F8F6)
private val TextoDetalle = Color(0xFF344B35)
private val RojoCancelacion = Color(0xFFA45D50)

@Composable
fun ReservationDetailScreen(
    reserva: Reserva,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onCancel: () -> Unit
) {
    var estado by remember(reserva.id, reserva.estado) {
        mutableStateOf(reserva.estado)
    }
    var mostrarConfirmacion by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FondoDetalle)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Encabezado
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = VerdeDetalle
                )
            }

            Text(
                text = "Detalle de reserva",
                style = MaterialTheme.typography.headlineSmall,
                color = TextoDetalle,
                fontWeight = FontWeight.Bold
            )
        }

        // Tarjeta principal
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

                // Estado, servicio e identificador
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = if (estado.equals("Cancelada", ignoreCase = true))
                            RojoCancelacion.copy(alpha = 0.12f)
                        else
                            VerdeDetalle.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = estado,
                            color = if (estado.equals("Cancelada", ignoreCase = true))
                                RojoCancelacion
                            else
                                VerdeDetalle,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 6.dp
                            )
                        )
                    }

                    Text(
                        text = reserva.servicio,
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextoDetalle,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Reserva #${reserva.id.toString().padStart(4, '0')}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                HorizontalDivider(color = Color(0xFFE4E7DF))

                // Información de la sesión
                Text(
                    text = "Información de la sesión",
                    style = MaterialTheme.typography.titleMedium,
                    color = VerdeDetalle,
                    fontWeight = FontWeight.SemiBold
                )

                ReservationInfoRow(
                    icon = Icons.Default.Spa,
                    label = "Especialidad",
                    value = reserva.especialidad
                )

                ReservationInfoRow(
                    icon = Icons.Default.Person,
                    label = "Profesional",
                    value = reserva.profesional
                )

                ReservationInfoRow(
                    icon = Icons.Default.CalendarMonth,
                    label = "Fecha",
                    value = reserva.fecha.format(
                        DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy")
                            .withLocale(java.util.Locale.forLanguageTag("es-CL"))
                    )
                )

                ReservationInfoRow(
                    icon = Icons.Default.AccessTime,
                    label = "Horario",
                    value = "${reserva.hora} hrs"
                )

                ReservationInfoRow(
                    icon = Icons.Default.Timer,
                    label = "Duración",
                    value = "${reserva.duracionMinutos} minutos"
                )

                ReservationInfoRow(
                    icon = Icons.Default.Info,
                    label = "Modalidad",
                    value = reserva.modalidad
                )

                // Cancelar reserva
                if (
                    estado.equals("Confirmada", ignoreCase = true) ||
                    estado.equals("Pendiente", ignoreCase = true)
                ) {

                    HorizontalDivider(color = Color(0xFFE4E7DF))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = {
                                mostrarConfirmacion = true
                            },
                            contentPadding = PaddingValues(
                                horizontal = 4.dp,
                                vertical = 0.dp
                            )
                        ) {
                            Text(
                                text = "Cancelar reserva",
                                color = RojoCancelacion,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                tint = RojoCancelacion,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // Aviso de cancelación
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFEEF1E9)
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = VerdeDetalle
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Importante",
                        color = VerdeDetalle,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "Las cancelaciones deben realizarse con al menos 12 horas de anticipación respecto al horario de la sesión.",
                        color = VerdeDetalle,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }

    // Diálogo de confirmación
    if (mostrarConfirmacion) {
        AlertDialog(
            onDismissRequest = {
                mostrarConfirmacion = false
            },
            title = {
                Text(
                    text = "Cancelar reserva",
                    color = TextoDetalle
                )
            },
            text = {
                Text(
                    text = "¿Estás seguro de que deseas cancelar esta reserva?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onCancel()
                        mostrarConfirmacion = false
                    }
                ) {
                    Text(
                        text = "Sí, cancelar",
                        color = RojoCancelacion
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarConfirmacion = false
                    }
                ) {
                    Text(
                        text = "Volver",
                        color = VerdeDetalle
                    )
                }
            },
            containerColor = FondoDetalle
        )
    }
}

@Composable
private fun ReservationInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = VerdeDetalle,
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = label,
                color = Color.Gray,
                style = MaterialTheme.typography.labelSmall
            )

            Text(
                text = value,
                color = TextoDetalle,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
