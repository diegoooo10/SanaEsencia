
package com.example.sanaescencia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private val VerdeDetalleProducto = Color(0xFF617A56)
private val FondoDetalleProducto = Color(0xFFF9F8F6)
private val TextoDetalleProducto = Color(0xFF344B35)

private data class ProductDetail(
    val id: Int,
    val name: String,
    val category: String,
    val price: String,
    val description: String,
    val icon: ImageVector
)

private val productDetails = listOf(
    ProductDetail(
        1,
        "Crema hidratante",
        "Cuidado facial",
        "$12.990",
        "Crema diseñada para complementar tu rutina diaria de cuidado facial, ayudando a mantener la piel hidratada y suave.",
        Icons.Default.Face
    ),
    ProductDetail(
        2,
        "Sérum facial",
        "Cuidado facial",
        "$15.990",
        "Producto para complementar el cuidado diario del rostro y aportar una experiencia de cuidado facial.",
        Icons.Default.WaterDrop
    ),
    ProductDetail(
        3,
        "Limpiador facial",
        "Cuidado facial",
        "$9.990",
        "Producto de limpieza facial pensado para formar parte de tu rutina diaria de cuidado de la piel.",
        Icons.Default.Spa
    ),
    ProductDetail(
        4,
        "Exfoliante facial",
        "Cuidado facial",
        "$11.990",
        "Producto para complementar la limpieza del rostro y ayudar a retirar las células muertas de la superficie.",
        Icons.Default.Face
    ),
    ProductDetail(
        5,
        "Crema corporal",
        "Cuidado corporal",
        "$13.990",
        "Crema para el cuidado corporal, ideal para complementar tu rutina de hidratación.",
        Icons.Default.Spa
    ),
    ProductDetail(
        6,
        "Aceite corporal",
        "Cuidado corporal",
        "$10.990",
        "Aceite pensado para el cuidado de la piel y para complementar momentos de relajación.",
        Icons.Default.WaterDrop
    ),
    ProductDetail(
        7,
        "Aceite esencial",
        "Aromaterapia",
        "$8.990",
        "Esencia para complementar experiencias de aromaterapia y bienestar.",
        Icons.Default.WaterDrop
    ),
    ProductDetail(
        8,
        "Difusor aromático",
        "Aromaterapia",
        "$18.990",
        "Accesorio para distribuir aromas y crear un ambiente agradable en tus espacios.",
        Icons.Default.Spa
    ),
    ProductDetail(
        9,
        "Sales de baño",
        "Bienestar",
        "$8.990",
        "Producto para complementar tus momentos de relajación y cuidado personal.",
        Icons.Default.Favorite
    ),
    ProductDetail(
        10,
        "Kit de bienestar",
        "Bienestar",
        "$22.990",
        "Conjunto de productos pensado para acompañar tus momentos de autocuidado.",
        Icons.Default.Favorite
    )
)

@Composable
fun ProductDetailScreen(
    productId: Int,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onAddToCart: (Int) -> Unit = {}
) {

    val product = productDetails.find {
        it.id == productId
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FondoDetalleProducto)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
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
                    tint = VerdeDetalleProducto
                )
            }

            Text(
                text = "Detalle del producto",
                style = MaterialTheme.typography.headlineSmall,
                color = TextoDetalleProducto,
                fontWeight = FontWeight.Bold
            )
        }

        if (product == null) {

            Text(
                text = "No se encontró el producto.",
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium
            )

        } else {

            // Imagen principal temporal
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFEEF1E9)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = product.icon,
                        contentDescription = product.name,
                        tint = VerdeDetalleProducto,
                        modifier = Modifier.size(90.dp)
                    )
                }
            }

            // Nombre y categoría
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {

                Text(
                    text = product.category,
                    color = VerdeDetalleProducto,
                    style = MaterialTheme.typography.labelLarge
                )

                Text(
                    text = product.name,
                    color = TextoDetalleProducto,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = product.price,
                    color = VerdeDetalleProducto,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            HorizontalDivider(
                color = Color(0xFFE4E7DF)
            )

            // Descripción
            Text(
                text = "Descripción",
                color = TextoDetalleProducto,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = product.description,
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium
            )

            // Disponibilidad
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = VerdeDetalleProducto
                )

                Text(
                    text = "Disponible",
                    color = VerdeDetalleProducto,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Agregar al carrito
            Button(
                onClick = {
                    onAddToCart(product.id)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = VerdeDetalleProducto
                )
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Agregar al carrito",
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }
        }
    }
}
