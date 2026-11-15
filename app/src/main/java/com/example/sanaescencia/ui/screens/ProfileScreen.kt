
package com.example.sanaescencia.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.ui.layout.ContentScale
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import com.example.sanaescencia.ui.components.CameraScreen
import androidx.compose.ui.graphics.asImageBitmap

private val VerdePerfil = Color(0xFF617A56)
private val FondoPerfil = Color(0xFFF9F8F6)
private val TextoPerfil = Color(0xFF344B35)
private val RojoPerfil = Color(0xFFA45D50)

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onEditProfile: () -> Unit = {},
    onChangePassword: () -> Unit = {},
    onLogout: () -> Unit = {}
) {

    var profileImage by remember {
        mutableStateOf<Bitmap?>(null)
    }

    var mostrarCamara by remember {
        mutableStateOf(false)
    }

    if (mostrarCamara) {
        CameraScreen(
            onPhotoCaptured = { bitmap ->
                profileImage = bitmap
                mostrarCamara = false
            },
            onClose = {
                mostrarCamara = false
            }
        )
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FondoPerfil)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Encabezado
        Text(
            text = "Mi perfil",
            style = MaterialTheme.typography.headlineMedium,
            color = TextoPerfil,
            fontWeight = FontWeight.Bold
        )

        // Información del cliente
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Surface(
                modifier = Modifier
                    .size(100.dp)
                    .clickable {
                        mostrarCamara = true
                    },
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFE6EBE1)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    profileImage?.let { bitmap ->
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Fotografía de perfil",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(20.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } ?: run {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Perfil",
                            tint = VerdePerfil,
                            modifier = Modifier.size(42.dp)
                        )
                    }
                }
            }

            Text(
                text = "María González",
                style = MaterialTheme.typography.titleLarge,
                color = TextoPerfil,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Cliente Sanaesencia",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }

        // Información personal
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

                Text(
                    text = "Información personal",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextoPerfil,
                    fontWeight = FontWeight.SemiBold
                )

                ProfileInfoRow(
                    icon = Icons.Default.Person,
                    label = "Nombre completo",
                    value = "María González"
                )

                ProfileInfoRow(
                    icon = Icons.Default.Email,
                    label = "Correo electrónico",
                    value = "maria@correo.cl"
                )

                ProfileInfoRow(
                    icon = Icons.Default.Phone,
                    label = "Teléfono",
                    value = "+56 9 1234 5678"
                )

                ProfileInfoRow(
                    icon = Icons.Default.LocationOn,
                    label = "Ciudad",
                    value = "Villa Alemana"
                )

                ProfileInfoRow(
                    icon = Icons.Default.CalendarMonth,
                    label = "Fecha de nacimiento",
                    value = "01-01-1995"
                )

                HorizontalDivider(
                    color = Color(0xFFE4E7DF)
                )

                TextButton(
                    onClick = onEditProfile,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text = "Editar información",
                        color = VerdePerfil,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = VerdePerfil
                    )
                }
            }
        }

        // Opciones de cuenta
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Text(
                    text = "Cuenta",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextoPerfil,
                    fontWeight = FontWeight.SemiBold
                )

                TextButton(
                    onClick = onChangePassword,
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(
                        horizontal = 4.dp,
                        vertical = 8.dp
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = VerdePerfil
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Cambiar contraseña",
                        color = TextoPerfil,
                        modifier = Modifier.weight(1f)
                    )
                }

                HorizontalDivider(
                    color = Color(0xFFE4E7DF)
                )

                TextButton(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(
                        horizontal = 4.dp,
                        vertical = 8.dp
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = null,
                        tint = RojoPerfil
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Cerrar sesión",
                        color = RojoPerfil,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(
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
            tint = VerdePerfil,
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
                color = TextoPerfil,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
