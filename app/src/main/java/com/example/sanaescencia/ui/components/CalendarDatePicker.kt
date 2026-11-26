package com.example.sanaescencia.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CalendarDatePicker(
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val verde = Color(0xFF617A56)
    val fondo = Color(0xFFF9F8F6)
    val verdeClaro = Color(0xFFE8EEE4)

    val today = LocalDate.now()

    var currentMonth by remember {
        mutableStateOf(YearMonth.from(selectedDate ?: today))
    }

    var temporalDate by remember {
        mutableStateOf(selectedDate ?: today)
    }

    val monthFormatter = DateTimeFormatter.ofPattern(
        "MMMM yyyy",
        Locale("es", "CL")
    )

    val weekDays = listOf("L", "M", "M", "J", "V", "S", "D")

    val firstDay = currentMonth.atDay(1)
    val offset = firstDay.dayOfWeek.value - 1
    val daysInMonth = currentMonth.lengthOfMonth()
    val totalCells = ((offset + daysInMonth + 6) / 7) * 7

    Dialog(onDismissRequest = onDismiss) {

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = fondo
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Text(
                    text = "Seleccionar fecha",
                    color = verde,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Escoge el día de tu próxima sesión.",
                    color = Color.Gray,
                    fontSize = 14.sp
                )

                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        // Navegación entre meses
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            TextButton(
                                enabled = currentMonth > YearMonth.from(today),
                                onClick = {
                                    currentMonth = currentMonth.minusMonths(1)
                                }
                            ) {
                                Text(
                                    text = "‹",
                                    fontSize = 28.sp
                                )
                            }

                            Text(
                                text = currentMonth.format(monthFormatter)
                                    .replaceFirstChar { it.titlecase() },
                                color = verde,
                                fontWeight = FontWeight.SemiBold
                            )

                            TextButton(
                                onClick = {
                                    currentMonth = currentMonth.plusMonths(1)
                                }
                            ) {
                                Text(
                                    text = "›",
                                    fontSize = 28.sp,
                                    color = verde
                                )
                            }
                        }

                        // Encabezado de días
                        Row(modifier = Modifier.fillMaxWidth()) {
                            weekDays.forEach { day ->
                                Box(
                                    modifier = Modifier.weight(1f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = day,
                                        color = Color.Gray,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }

                        // Días del calendario
                        for (week in 0 until totalCells / 7) {

                            Row(modifier = Modifier.fillMaxWidth()) {

                                for (column in 0 until 7) {

                                    val day = week * 7 + column - offset + 1

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1f)
                                            .padding(2.dp),
                                        contentAlignment = Alignment.Center
                                    ) {

                                        if (day in 1..daysInMonth) {

                                            val date = currentMonth.atDay(day)
                                            val enabled = !date.isBefore(today)
                                            val isSelected = date == temporalDate

                                            val backgroundColor = when {
                                                isSelected -> verde
                                                enabled -> verdeClaro
                                                else -> Color.Transparent
                                            }

                                            val textColor = when {
                                                isSelected -> Color.White
                                                enabled -> verde
                                                else -> Color.LightGray
                                            }

                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .clip(CircleShape)
                                                    .background(backgroundColor)
                                                    .clickable(enabled = enabled) {
                                                        temporalDate = date
                                                    },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = day.toString(),
                                                    color = textColor,
                                                    fontSize = 14.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Confirmar selección
                Button(
                    onClick = {
                        onDateSelected(temporalDate)
                    },
                    enabled = !temporalDate.isBefore(today),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = verde,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Confirmar fecha")
                }

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Cancelar",
                        color = Color.Gray
                    )
                }
            }
        }
    }
}