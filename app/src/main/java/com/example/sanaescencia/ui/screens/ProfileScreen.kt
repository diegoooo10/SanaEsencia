
    package com.example.sanaescencia.ui.screens

    import android.graphics.Bitmap
    import android.graphics.BitmapFactory
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
    import com.example.sanaescencia.data.remote.dto.UsuarioResponse
    import androidx.compose.ui.platform.LocalContext
    import kotlinx.coroutines.Dispatchers
    import kotlinx.coroutines.launch
    import kotlinx.coroutines.withContext
    import java.io.File

    private val VerdePerfil = Color(0xFF617A56)
    private val FondoPerfil = Color(0xFFF9F8F6)
    private val TextoPerfil = Color(0xFF344B35)
    private val RojoPerfil = Color(0xFFA45D50)

    @Composable
    fun ProfileScreen(
        usuario: UsuarioResponse?,
        modifier: Modifier = Modifier,
        onEditProfile: () -> Unit = {},
        onChangePassword: () -> Unit = {},
        onLogout: () -> Unit = {}
    ) {

        val context = LocalContext.current
        val scope = rememberCoroutineScope()

        var profileImage by remember {
            mutableStateOf<Bitmap?>(null)
        }

        var mostrarCamara by remember {
            mutableStateOf(false)
        }

        var mostrarDialogoEliminar by remember {
            mutableStateOf(false)
        }

        // Recuperar fotografía guardada
        LaunchedEffect(Unit) {
            val imageFile = File(context.filesDir, "foto_perfil.jpg")

            if (imageFile.exists()) {
                profileImage = withContext(Dispatchers.IO) {
                    BitmapFactory.decodeFile(imageFile.absolutePath)
                }
            }
        }

        if (mostrarCamara) {
            CameraScreen(
                onPhotoCaptured = { bitmap ->

                    scope.launch {
                        val imageFile = File(
                            context.filesDir,
                            "foto_perfil.jpg"
                        )

                        val guardado = withContext(Dispatchers.IO) {
                            runCatching {
                                imageFile.outputStream().use { output ->
                                    check(
                                        bitmap.compress(
                                            Bitmap.CompressFormat.JPEG,
                                            90,
                                            output
                                        )
                                    )
                                }
                            }.isSuccess
                        }

                        if (guardado) {
                            profileImage = bitmap
                            mostrarCamara = false
                        }
                    }
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

                // Eliminar fotografía
                if (profileImage != null) {
                    TextButton(
                        onClick = {
                            mostrarDialogoEliminar = true
                        }
                    ) {
                        Text(
                            text = "Eliminar fotografía",
                            color = RojoPerfil
                        )
                    }
                }

                Text(
                    text = usuario?.let { "${it.nombre} ${it.apellido}" } ?: "Cargando perfil...",
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
                        value = usuario?.let { "${it.nombre} ${it.apellido}" } ?: "No disponible"
                    )

                    ProfileInfoRow(
                        icon = Icons.Default.Email,
                        label = "Correo electrónico",
                        value = usuario?.correo ?: "No disponible"
                    )

                    ProfileInfoRow(
                        icon = Icons.Default.Phone,
                        label = "Teléfono",
                        value = usuario?.telefono ?: "No registrado"
                    )

                    ProfileInfoRow(
                        icon = Icons.Default.LocationOn,
                        label = "Dirección",
                        value = usuario?.direccion ?: "No registrada"
                    )

                    ProfileInfoRow(
                        icon = Icons.Default.CalendarMonth,
                        label = "Fecha de nacimiento",
                        value = usuario?.fechaNacimiento
                            ?.split("-")
                            ?.takeIf { it.size == 3 }
                            ?.let { "${it[2]}-${it[1]}-${it[0]}" }
                            ?: "No disponible"
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
        // Confirmación para eliminar fotografía
        if (mostrarDialogoEliminar) {
            AlertDialog(
                onDismissRequest = {
                    mostrarDialogoEliminar = false
                },
                title = {
                    Text("Eliminar fotografía")
                },
                text = {
                    Text(
                        "¿Estás seguro de que deseas eliminar tu fotografía de perfil?"
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            scope.launch {
                                withContext(Dispatchers.IO) {
                                    File(
                                        context.filesDir,
                                        "foto_perfil.jpg"
                                    ).delete()
                                }

                                profileImage = null
                                mostrarDialogoEliminar = false
                            }
                        }
                    ) {
                        Text("Eliminar", color = RojoPerfil)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            mostrarDialogoEliminar = false
                        }
                    ) {
                        Text("Conservar")
                    }
                }
            )
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
