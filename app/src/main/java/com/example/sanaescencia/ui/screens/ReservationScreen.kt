
package com.example.sanaescencia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sanaescencia.data.model.Reserva

private val VerdeSanaesencia = Color(0xFF617A56)
private val FondoSanaesencia = Color(0xFFF9F8F6)
@Composable
fun ReservationScreen(
    reservas: List<Reserva> = emptyList(),
    modifier: Modifier = Modifier,
    onNewReservation: () -> Unit = {},
    onReservationDetails: (Int) -> Unit = {}
) {

    var selectedTab by remember { mutableIntStateOf(0) }

    val filteredReservations = if (selectedTab == 0) {
        reservas.filter {
            it.estado == "Confirmada" || it.estado == "Pendiente"
        }
    } else {
        reservas.filter {
            it.estado == "Realizada"
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FondoSanaesencia)
            .padding(16.dp)
    ) {

        // Encabezado
        Text(
            text = "Mis reservas",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFF344B35),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Consulta y administra tus próximas sesiones.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Pestañas
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = FondoSanaesencia,
            contentColor = VerdeSanaesencia
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Próximas") }
            )

            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Historial") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de reservas
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            if (filteredReservations.isEmpty()) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (selectedTab == 0)
                            "No tienes próximas reservas."
                        else
                            "Todavía no tienes sesiones realizadas.",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

            } else {

                filteredReservations.forEach { reserva ->

                    ReservationCard(
                        reserva = reserva,
                        onClick = {
                            onReservationDetails(reserva.id)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Nueva reserva
        Button(
            onClick = onNewReservation,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = VerdeSanaesencia
            )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Nueva reserva",
                modifier = Modifier.padding(vertical = 6.dp)
            )
        }
    }
}

@Composable
private fun ReservationCard(
    reserva: Reserva,
    onClick: () -> Unit
) {

    val statusColor = when (reserva.estado) {
        "Confirmada" -> Color(0xFF52734C)
        "Pendiente" -> Color(0xFF9A7839)
        else -> Color(0xFF777D73)
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Servicio y estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = reserva.servicio,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF344B35)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    color = statusColor.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = reserva.estado,
                        color = statusColor,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 6.dp
                        )
                    )
                }
            }

            HorizontalDivider(color = Color(0xFFE4E7DF))

            ReservationDetail(
                icon = Icons.Default.Spa,
                label = "Especialidad",
                value = reserva.especialidad
            )

            ReservationDetail(
                icon = Icons.Default.Person,
                label = "Profesional",
                value = reserva.profesional
            )

            ReservationDetail(
                icon = Icons.Default.CalendarMonth,
                label = "Fecha",
                value = reserva.fecha
            )

            ReservationDetail(
                icon = Icons.Default.AccessTime,
                label = "Horario",
                value = "${reserva.hora} hrs"
            )

            HorizontalDivider(color = Color(0xFFE4E7DF))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ver detalles",
                    color = VerdeSanaesencia,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.width(4.dp))

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
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
            tint = VerdeSanaesencia,
            modifier = Modifier.size(20.dp)
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
                color = Color(0xFF344B35),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
