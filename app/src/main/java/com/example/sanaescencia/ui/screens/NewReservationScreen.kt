
package com.example.sanaescencia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sanaescencia.ui.components.ReservationConfirmationDialog

// Modelos de prueba

private data class Specialty(
    val id: Int,
    val name: String,
    val description: String
)

private data class Professional(
    val id: Int,
    val name: String,
    val specialties: List<Int>
)

private data class Service(
    val id: Int,
    val name: String,
    val specialtyId: Int,
    val professionalIds: List<Int>,
    val duration: Int
)

private data class ReservationDate(
    val value: String,
    val label: String
)

private val specialties = listOf(
    Specialty(1, "Masoterapia", "Terapias y tratamientos corporales"),
    Specialty(2, "Estética", "Tratamientos de cuidado personal"),
    Specialty(3, "Terapias integrales", "Bienestar y equilibrio")
)

private val professionals = listOf(
    Professional(1, "Profesional A", listOf(1, 3)),
    Professional(2, "Profesional B", listOf(1)),
    Professional(3, "Profesional C", listOf(2)),
    Professional(4, "Profesional D", listOf(2, 3))
)

private val services = listOf(
    Service(1, "Masaje de relajación", 1, listOf(1, 2), 60),
    Service(2, "Masaje descontracturante", 1, listOf(1, 2), 60),
    Service(3, "Tratamiento facial", 2, listOf(3), 45),
    Service(4, "Limpieza facial", 2, listOf(3), 60),
    Service(5, "Terapia de relajación", 3, listOf(1, 4), 60),
    Service(6, "Terapia integral", 3, listOf(4), 90)
)

private val reservationDates = listOf(
    ReservationDate("2026-11-17", "Mar 17"),
    ReservationDate("2026-11-18", "Mié 18"),
    ReservationDate("2026-11-19", "Jue 19"),
    ReservationDate("2026-11-20", "Vie 20"),
    ReservationDate("2026-11-21", "Sáb 21")
)

private val availableHours = listOf(
    "10:00", "11:30", "14:00", "15:30", "16:00", "17:30"
)

@Composable
fun NewReservationScreen(
    modifier: Modifier = Modifier
) {

    val verde = Color(0xFF617A56)
    val fondo = Color(0xFFF9F8F6)

    var selectedSpecialty by remember { mutableStateOf<Specialty?>(null) }
    var selectedProfessional by remember { mutableStateOf<Professional?>(null) }
    var selectedService by remember { mutableStateOf<Service?>(null) }
    var selectedModality by remember { mutableStateOf<String?>(null) }
    var selectedDate by remember { mutableStateOf<ReservationDate?>(null) }
    var selectedHour by remember { mutableStateOf<String?>(null) }
    var showConfirmationDialog by remember { mutableStateOf(false) }

    val filteredProfessionals = professionals.filter {
        selectedSpecialty != null &&
                selectedSpecialty!!.id in it.specialties
    }

    val filteredServices = services.filter {
        selectedSpecialty != null &&
                selectedProfessional != null &&
                it.specialtyId == selectedSpecialty!!.id &&
                selectedProfessional!!.id in it.professionalIds
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(fondo)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        // Título
        Text(
            text = "Nueva reserva",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFF344B35),
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Selecciona tu atención y encuentra el horario que mejor te acomode.",
            color = Color.Gray,
            style = MaterialTheme.typography.bodyMedium
        )

        HorizontalDivider(color = Color(0xFFE4E7DF))

        // 1. Especialidad
        SectionTitle("1. Selecciona una especialidad")

        specialties.forEach { specialty ->
            SelectionCard(
                title = specialty.name,
                description = specialty.description,
                selected = selectedSpecialty?.id == specialty.id,
                color = verde,
                onClick = {
                    if (selectedSpecialty?.id != specialty.id) {
                        selectedSpecialty = specialty
                        selectedProfessional = null
                        selectedService = null
                        selectedModality = null
                        selectedDate = null
                        selectedHour = null
                    }
                }
            )
        }

        // 2. Profesionales filtrados
        if (selectedSpecialty != null) {

            HorizontalDivider(color = Color(0xFFE4E7DF))

            SectionTitle("2. Selecciona un profesional")

            Text(
                text = "Profesionales de ${selectedSpecialty!!.name}",
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
            )

            filteredProfessionals.forEach { professional ->
                SelectionCard(
                    title = professional.name,
                    description = "Profesional de ${selectedSpecialty!!.name}",
                    selected = selectedProfessional?.id == professional.id,
                    color = verde,
                    onClick = {
                        if (selectedProfessional?.id != professional.id) {
                            selectedProfessional = professional
                            selectedService = null
                            selectedModality = null
                            selectedDate = null
                            selectedHour = null
                        }
                    }
                )
            }
        }

        // 3. Servicios
        if (selectedProfessional != null) {

            HorizontalDivider(color = Color(0xFFE4E7DF))

            SectionTitle("3. Selecciona un servicio")

            filteredServices.forEach { service ->
                SelectionCard(
                    title = service.name,
                    description = "Duración: ${service.duration} minutos",
                    selected = selectedService?.id == service.id,
                    color = verde,
                    onClick = {
                        if (selectedService?.id != service.id) {
                            selectedService = service
                            selectedModality = null
                            selectedDate = null
                            selectedHour = null
                        }
                    }
                )
            }
        }

        // 4. Modalidad de atención
        if (selectedService != null) {

            HorizontalDivider(color = Color(0xFFE4E7DF))

            SectionTitle("4. Selecciona la modalidad")

            SelectionCard(
                title = "Presencial",
                description = "Atención en nuestro centro Sanaesencia",
                selected = selectedModality == "Presencial",
                color = verde,
                onClick = {
                    selectedModality = "Presencial"
                    selectedDate = null
                    selectedHour = null
                }
            )

            SelectionCard(
                title = "Online",
                description = "Atención mediante videollamada",
                selected = selectedModality == "Online",
                color = verde,
                onClick = {
                    selectedModality = "Online"
                    selectedDate = null
                    selectedHour = null
                }
            )
        }

        // 4. Fecha
        if (selectedService != null && selectedModality != null) {

            HorizontalDivider(color = Color(0xFFE4E7DF))

            SectionTitle("5. Selecciona fecha y horario")

            Text(
                text = "Fechas disponibles",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                reservationDates.forEach { date ->
                    FilterChip(
                        selected = selectedDate?.value == date.value,
                        onClick = {
                            if (selectedDate?.value != date.value) {
                                selectedDate = date
                                selectedHour = null
                            }
                        },
                        label = {
                            Text(date.label)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 6. Horarios
        if (selectedDate != null) {

            Text(
                text = "Horarios disponibles",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                availableHours.chunked(3).forEach { hours ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        hours.forEach { hour ->
                            FilterChip(
                                selected = selectedHour == hour,
                                onClick = {
                                    selectedHour = hour
                                },
                                label = {
                                    Text(hour)
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        repeat(3 - hours.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        // 7. Resumen
        if (
            selectedSpecialty != null &&
            selectedProfessional != null &&
            selectedService != null &&
            selectedModality != null &&
            selectedDate != null &&
            selectedHour != null
        ) {

            HorizontalDivider(color = Color(0xFFE4E7DF))

            SectionTitle("Resumen de reserva")

            Card(
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
                    SummaryRow("Especialidad", selectedSpecialty!!.name)
                    SummaryRow("Profesional", selectedProfessional!!.name)
                    SummaryRow("Servicio", selectedService!!.name)
                    SummaryRow("Modalidad", selectedModality!!)
                    SummaryRow("Duración", "${selectedService!!.duration} minutos")
                    SummaryRow("Fecha", selectedDate!!.label)
                    SummaryRow("Hora", selectedHour!!)
                }
            }

            Button(
                onClick = {
                    showConfirmationDialog = true
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = verde
                )
            ) {
                Text(
                    text = "Confirmar reserva",
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }

    // Diálogo de confirmación
    if (showConfirmationDialog) {
        ReservationConfirmationDialog(
            service = selectedService?.name.orEmpty(),
            professional = selectedProfessional?.name.orEmpty(),
            date = selectedDate?.label.orEmpty(),
            hour = selectedHour.orEmpty(),
            modality = selectedModality.orEmpty(),
            onDismiss = {
                showConfirmationDialog = false
            }
        )
    }
}

// Componente reutilizable para los títulos

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = Color(0xFF526C49),
        fontWeight = FontWeight.SemiBold
    )
}

// Tarjeta seleccionable

@Composable
private fun SelectionCard(
    title: String,
    description: String,
    selected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected)
                Color(0xFFE7EDDF)
            else
                Color.White
        ),
        border = if (selected)
            androidx.compose.foundation.BorderStroke(1.dp, color)
        else
            null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    color = Color(0xFF344B35),
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = description,
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            RadioButton(
                selected = selected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = color
                )
            )
        }
    }
}

// Componente reutilizable para el resumen

@Composable
private fun SummaryRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color.Gray,
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = value,
            color = Color(0xFF344B35),
            fontWeight = FontWeight.Medium,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
