
package com.example.sanaescencia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.sp
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
import com.example.sanaescencia.ui.components.CalendarDatePicker
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

// Precio fijo para la demo
private const val MONTO_SERVICIO = 25_000

private data class ReservationDate(
    val value: String,
    val label: String
)

@OptIn(ExperimentalMaterial3Api::class)
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
    val inicioPagoState by reservaViewModel.inicioPagoState.collectAsState()

    var procesandoPago by remember {
        mutableStateOf(false)
    }
    var selectedSpecialty by remember { mutableStateOf<Especialidad?>(null) }
    var selectedProfessional by remember { mutableStateOf<Profesional?>(null) }
    var selectedService by remember { mutableStateOf<Servicio?>(null) }
    var selectedModality by remember { mutableStateOf<String?>(null) }
    var selectedDate by remember { mutableStateOf<ReservationDate?>(null) }
    var selectedHour by remember { mutableStateOf<String?>(null) }
    var showConfirmationDialog by remember { mutableStateOf(false) }

    var showDatePicker by remember { mutableStateOf(false) }

    val today = LocalDate.now()

    var startDate by remember { mutableStateOf(today) }

    val formatter = DateTimeFormatter.ofPattern(
        "EEE d MMM", Locale("es", "CL")
    )

    val reservationDates = List(5) { index ->
        val date = startDate.plusDays(index.toLong())

        ReservationDate(
            value = date.toString(),
            label = date.format(formatter)
                .replaceFirstChar { it.titlecase() }
        )
    }

    LaunchedEffect(Unit) {
        reservaViewModel.limpiarCreacion()
    }

    fun seleccionarFecha(date: ReservationDate) {
        if (selectedDate?.value == date.value) return

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


    LaunchedEffect(crearReservaState) {
        crearReservaState?.let { resultado ->

            resultado.onSuccess { reserva ->
                reservaViewModel.iniciarPago(
                    token = token,
                    reservaId = reserva.id
                )
            }

            resultado.onFailure {
                procesandoPago = false
            }
        }
    }

    LaunchedEffect(inicioPagoState) {
        if (inicioPagoState?.isFailure == true) {
            procesandoPago = false
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
                text = "Selecciona una fecha",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = verde
                )
            ) {
                Text("Seleccionar fecha en calendario")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                reservationDates.forEach { date ->

                    val localDate = LocalDate.parse(date.value)
                    val isSelected = selectedDate?.value == date.value

                    val dayName = localDate.format(
                        DateTimeFormatter.ofPattern("EEE", Locale("es", "CL"))
                    ).replace(".", "").replaceFirstChar { it.titlecase() }

                    val monthName = localDate.format(
                        DateTimeFormatter.ofPattern("MMM", Locale("es", "CL"))
                    ).replace(".", "").replaceFirstChar { it.titlecase() }

                    val textColor = if (isSelected) Color.White else verde

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .height(84.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) verde
                                else Color(0xFFE8EEE4)
                            )
                            .clickable {
                                seleccionarFecha(date)
                            },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = dayName,
                            color = textColor,
                            fontSize = 12.sp,
                            maxLines = 1,
                            softWrap = false
                        )

                        Text(
                            text = localDate.dayOfMonth.toString(),
                            color = textColor,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )

                        Text(
                            text = monthName,
                            color = textColor,
                            fontSize = 12.sp,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
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
                    SummaryRow(
                        "Fecha",
                        LocalDate.parse(selectedDate!!.value)
                            .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                    )
                    SummaryRow("Hora", selectedHour!!)

                    HorizontalDivider(color = Color(0xFFE4E7DF))

                    SummaryRow(
                        "Total a pagar",
                        "$${String.format(Locale.GERMANY, "%,d", MONTO_SERVICIO)}"
                    )
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
                        !procesandoPago &&
                        profesional != null &&
                        servicio != null &&
                        fecha != null &&
                        hora != null &&
                        modalidad != null &&
                        token.isNotBlank()
                    ) {
                        procesandoPago = true

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
                enabled = !procesandoPago && !isLoadingCrear,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = verde,
                    disabledContainerColor = verde.copy(alpha = 0.7f)
                )
            ) {
                if (procesandoPago) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = "Redirigiendo a Webpay...",
                            color = Color.White
                        )
                    }
                } else {
                    Text(
                        text = "Continuar y pagar",
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }

    if (showDatePicker) {
        CalendarDatePicker(
            selectedDate = selectedDate?.let {
                LocalDate.parse(it.value)
            },
            onDateSelected = { date ->
                startDate = date

                seleccionarFecha(
                    ReservationDate(
                        value = date.toString(),
                        label = date.format(formatter)
                            .replaceFirstChar { it.titlecase() }
                    )
                )

                showDatePicker = false
            },
            onDismiss = {
                showDatePicker = false
            }
        )
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
