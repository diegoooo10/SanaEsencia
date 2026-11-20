
package com.example.sanaescencia.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.sanaescencia.R

@Composable
fun ReservationCancelledDialog(
    onVolverReservas: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F8F6))
            .padding(28.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.logo_header),
            contentDescription = "Logo SanaEsencia",
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .offset(x = (-10).dp),
            contentScale = ContentScale.Fit
        )

        Text(
            text = "Reserva cancelada correctamente",
            style = MaterialTheme.typography.headlineSmall,
            color = Color(0xFF344C31),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 36.dp)
        )

        Text(
            text = "Tu reserva fue cancelada correctamente y el horario vuelve a estar disponible. Puedes agendar una nueva atención cuando quieras.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF52634E),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp, bottom = 36.dp)
        )

        Button(
            onClick = onVolverReservas,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF617A56)
            )
        ) {
            Text("Volver a reservas")
        }
    }
}
