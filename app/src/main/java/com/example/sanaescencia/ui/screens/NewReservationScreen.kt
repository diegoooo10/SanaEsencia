
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
import com.example.sanaescencia.data.model.Especialidad
import com.example.sanaescencia.data.model.Profesional
import com.example.sanaescencia.data.model.Servicio
import com.example.sanaescencia.ui.viewmodel.ReservaViewModel
import com.example.sanaescencia.data.remote.dto.ReservaRequest
import java.time.LocalDate

// Modelos de prueba

private data class ReservationDate(
    val value: String,
    val label: String
)

private val reservationDates = listOf(
    ReservationDate("2026-11-17", "Mar 17"),
    ReservationDate("2026-11-18", "Mié 18"),
    ReservationDate("2026-11-19", "Jue 19"),
    ReservationDate("2026-11-20", "Vie 20"),
    ReservationDate("2026-11-21", "Sáb 21")
)

@Composable
fun NewReservationScreen(
    profesionales: List<Profesional>,
    especialidades: List<Especialidad>,
    servicios: List<Servicio>,
    token: String,
    reservaViewModel: ReservaViewModel,
    modifier: Modifier = Modifier,
    onReservationConfirmed: () -> Unit = {}
){

    val verde = Color(0xFF617A56)
    val fondo = Color(0xFFF9F8F6)

    // Estado de disponibilidad de horarios
    val horariosState by reservaViewModel.horariosState.collectAsState()

    val isLoadingHorarios by
    reservaViewModel.isLoadingHorarios.collectAsState()
    val horariosDisponibles =
        horariosState?.getOrNull().orEmpty()
    val crearReservaState by reservaViewModel.crearReservaState.collectAsState()
    val isLoadingCrear by reservaViewModel.isLoadingCrear.collectAsState()
    var selectedSpecialty by remember { mutableStateOf<Especialidad?>(null) }
    var selectedProfessional by remember { mutableStateOf<Profesional?>(null) }
    var selectedService by remember { mutableStateOf<Servicio?>(null) }
    var selectedModality by remember { mutableStateOf<String?>(null) }
    var selectedDate by remember { mutableStateOf<ReservationDate?>(null) }
    var selectedHour by remember { mutableStateOf<String?>(null) }
    var showConfirmationDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        reservaViewModel.limpiarCreacion()
    }


    LaunchedEffect(crearReservaState) {
        crearReservaState?.onSuccess { reserva ->
            reservaViewModel.iniciarPago(
                token = token,
                reservaId = reserva.id
            )
        }
    }


    val filteredProfessionals = profesionales.filter {
    selectedSpecialty != null &&
            selectedSpecialty!!.id in it.especialidadesIds &&
            it.estadoAgenda == "DISPONIBLE"
}

val filteredServices = servicios.filter {
    selectedSpecialty != null &&
            it.especialidadId == selectedSpecialty!!.id
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

    especialidades
        .filter { specialty ->
            profesionales.any { professional ->
                specialty.id in professional.especialidadesIds &&
                        professional.estadoAgenda == "DISPONIBLE"
            }
        }
        .forEach { specialty ->
            SelectionCard(
                title = specialty.nombre,
                description = specialty.descripcion,
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

    // 2. Profesionales
    if (selectedSpecialty != null) {

        HorizontalDivider(color = Color(0xFFE4E7DF))

        SectionTitle("2. Selecciona un profesional")

        Text(
            text = "Profesionales de ${selectedSpecialty!!.nombre}",
            color = Color.Gray,
            style = MaterialTheme.typography.bodySmall
        )

        filteredProfessionals.forEach { professional ->
            SelectionCard(
                title = professional.nombre,
                description = professional.tituloProfesional,
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
                title = service.nombre,
                description = "Duración: ${service.duracionMinutos} minutos",
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


        // 4. Modalidad
        if (selectedService != null) {

            HorizontalDivider(color = Color(0xFFE4E7DF))

            SectionTitle("4. Selecciona la modalidad")

            selectedProfessional?.modalidades.orEmpty().forEach { modalidad ->

                SelectionCard(
                    title = modalidad,
                    description = if (modalidad == "Online")
                        "Atención mediante videollamada"
                    else
                        "Atención en nuestro centro Sanaesencia",
                    selected = selectedModality == modalidad,
                    color = verde,
                    onClick = {
                        if (selectedModality != modalidad) {
                            selectedModality = modalidad
                            selectedDate = null
                            selectedHour = null
                        }
                    }
                )
            }
        }


        // 5. Fecha
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

                                reservaViewModel.limpiarHorarios()

                                val profesional = selectedProfessional
                                val servicio = selectedService
                                val modalidad = selectedModality

                                if (
                                    profesional != null &&
                                    servicio != null &&
                                    modalidad != null &&
                                    token.isNotBlank()
                                ) {
                                    reservaViewModel.obtenerHorariosDisponibles(
                                        token = token,
                                        profesionalId = profesional.id.toLong(),
                                        servicioId = servicio.id.toLong(),
                                        fecha = LocalDate.parse(date.value),
                                        modalidad = modalidad
                                    )
                                }
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

// 6. Horarios
        if (selectedDate != null) {

            Text(
                text = "Horarios disponibles",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            when {
                isLoadingHorarios -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            color = verde,
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text("Consultando horarios...")
                    }
                }

                horariosState?.isFailure == true -> {
                    Text(
                        text = horariosState?.exceptionOrNull()?.message
                            ?: "No se pudieron consultar los horarios.",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                horariosState != null && horariosDisponibles.isEmpty() -> {
                    Text(
                        text = "No hay horarios disponibles para esta fecha.",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                horariosDisponibles.isNotEmpty() -> {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        horariosDisponibles.chunked(3).forEach { hours ->

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                hours.forEach { hour ->

                                    val hourText = hour.toString()

                                    FilterChip(
                                        selected = selectedHour == hourText,
                                        onClick = {
                                            selectedHour = hourText
                                        },
                                        label = {
                                            Text(hourText)
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
                    SummaryRow("Especialidad", selectedSpecialty!!.nombre)
                    SummaryRow("Profesional", selectedProfessional!!.nombre)
                    SummaryRow("Servicio", selectedService!!.nombre)
                    SummaryRow("Modalidad", selectedModality!!)
                    SummaryRow("Duración", "${selectedService!!.duracionMinutos} minutos")
                    SummaryRow("Fecha", selectedDate!!.label)
                    SummaryRow("Hora", selectedHour!!)
                }
            }

            Button(
                onClick = {
                    val profesional = selectedProfessional
                    val servicio = selectedService
                    val fecha = selectedDate
                    val hora = selectedHour
                    val modalidad = selectedModality

                    if (
                        profesional != null &&
                        servicio != null &&
                        fecha != null &&
                        hora != null &&
                        modalidad != null &&
                        token.isNotBlank()
                    ) {
                        val request = ReservaRequest(
                            profesionalId = profesional.id.toLong(),
                            servicioId = servicio.id.toLong(),
                            fecha = fecha.value,
                            hora = hora,
                            modalidad = modalidad
                        )

                        reservaViewModel.crearReserva(
                            token = token,
                            request = request
                        )
                    }
                },
                enabled = !isLoadingCrear,
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
            service = selectedService?.nombre.orEmpty(),
            professional = selectedProfessional?.nombre.orEmpty(),
            date = selectedDate?.label.orEmpty(),
            hour = selectedHour.orEmpty(),
            modality = selectedModality.orEmpty(),
            onDismiss = {
                showConfirmationDialog = false
            },
            onConfirm = {
                showConfirmationDialog = false
                onReservationConfirmed()
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
