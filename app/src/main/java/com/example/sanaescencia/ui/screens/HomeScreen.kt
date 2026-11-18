
package com.example.sanaescencia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.sanaescencia.data.model.Reserva

private val VerdeSanaesencia = Color(0xFF526C49)
private val FondoTarjeta = Color(0xFFE7EDDF)

@Composable
fun HomeScreen(
    nombreUsuario: String,
    reservas: List<Reserva> = emptyList(),
    modifier: Modifier = Modifier,
    onViewReservations: () -> Unit = {},
    onNewReservation: () -> Unit = {}
) {

    val proximaReserva = reservas
        .filter {
            it.estado.equals("Confirmada", ignoreCase = true) ||
                    it.estado.equals("Pendiente", ignoreCase = true)
        }
        .filter {
            !it.fecha.isBefore(java.time.LocalDate.now())
        }
        .sortedWith(
            compareBy<Reserva> { it.fecha }
                .thenBy { it.hora }
        )
        .firstOrNull()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        // Bienvenida
        Column {
            Text(
                text = "¡Hola, $nombreUsuario!",
                style = MaterialTheme.typography.headlineMedium,
                color = VerdeSanaesencia
            )

            Text(
                text = "Qué bueno tenerte de vuelta.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Próxima reserva
        if (proximaReserva != null) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = FondoTarjeta
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Text(
                        text = "PRÓXIMA RESERVA",
                        style = MaterialTheme.typography.labelLarge,
                        color = VerdeSanaesencia
                    )

                    Text(
                        text = proximaReserva.servicio,
                        style = MaterialTheme.typography.titleLarge,
                        color = VerdeSanaesencia
                    )

                    ReservationDetail(
                        icon = Icons.Default.CalendarToday,
                        text = proximaReserva.fecha.format(
                            java.time.format.DateTimeFormatter.ofPattern(
                                "EEEE dd 'de' MMMM",
                                java.util.Locale("es", "CL")
                            )
                        )
                    )

                    ReservationDetail(
                        icon = Icons.Default.AccessTime,
                        text = "${proximaReserva.hora} horas"
                    )

                    ReservationDetail(
                        icon = Icons.Default.LocationOn,
                        text = proximaReserva.modalidad
                    )

                    Button(
                        onClick = onViewReservations,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Ver mi reserva")
                    }
                }
            }

        } else {

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = FondoTarjeta
                ),
                onClick = onNewReservation
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Text(
                        text = "¿TIENES GANAS DE UNA NUEVA SESIÓN?",
                        style = MaterialTheme.typography.labelLarge,
                        color = VerdeSanaesencia
                    )

                    Text(
                        text = "Realiza una nueva reserva",
                        style = MaterialTheme.typography.titleLarge,
                        color = VerdeSanaesencia
                    )

                    Text(
                        text = "Agenda tu próxima sesión con nuestros profesionales.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Button(
                        onClick = onNewReservation,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Nueva reserva")
                    }
                }
            }
        }

        // Accesos rápidos
        Text(
            text = "¿Qué necesitas hoy?",
            style = MaterialTheme.typography.titleLarge,
            color = VerdeSanaesencia
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickAccessCard(
                title = "Nueva reserva",
                description = "Agenda tu próxima sesión",
                icon = Icons.Default.CalendarMonth,
                modifier = Modifier.weight(1f),
                onClick = onNewReservation
                )

            QuickAccessCard(
                title = "Mis sesiones",
                description = "Consulta tu historial",
                icon = Icons.Default.History,
                modifier = Modifier.weight(1f),
                onClick = onViewReservations
            )
        }

        // Beneficios
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.CardGiftcard,
                    contentDescription = "Beneficios",
                    tint = VerdeSanaesencia,
                    modifier = Modifier.size(30.dp)
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp)
                ) {
                    Text(
                        text = "Mis beneficios",
                        style = MaterialTheme.typography.titleMedium,
                        color = VerdeSanaesencia
                    )

                    Text(
                        text = "Revisa tus recompensas disponibles.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = VerdeSanaesencia
                )
            }
        }
    }
}

@Composable
private fun ReservationDetail(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        androidx.compose.material3.Icon(
            imageVector = icon,
            contentDescription = null,
            tint = VerdeSanaesencia
        )

        Text(
            text = text,
            color = VerdeSanaesencia,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun QuickAccessCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            androidx.compose.material3.Icon(
                imageVector = icon,
                contentDescription = null,
                tint = VerdeSanaesencia,
                modifier = Modifier.size(28.dp)
            )

            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = VerdeSanaesencia
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
