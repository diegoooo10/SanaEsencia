
package com.example.sanaescencia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val VerdeProductos = Color(0xFF617A56)
private val FondoProductos = Color(0xFFF9F8F6)
private val TextoProductos = Color(0xFF344B35)

private data class ShopProduct(
    val id: Int,
    val name: String,
    val description: String,
    val price: String,
    val categoryId: Int,
    val icon: ImageVector
)

private val shopProducts = listOf(
    // Cuidado facial
    ShopProduct(
        1, "Crema hidratante",
        "Hidratación diaria para la piel.",
        "$12.990", 1, Icons.Default.Face
    ),
    ShopProduct(
        2, "Sérum facial",
        "Complemento para tu rutina facial.",
        "$15.990", 1, Icons.Default.WaterDrop
    ),
    ShopProduct(
        3, "Limpiador facial",
        "Limpieza suave para el rostro.",
        "$9.990", 1, Icons.Default.Spa
    ),
    ShopProduct(
        4, "Exfoliante facial",
        "Renueva la superficie de la piel.",
        "$11.990", 1, Icons.Default.Face
    ),

    // Cuidado corporal
    ShopProduct(
        5, "Crema corporal",
        "Hidratación y suavidad corporal.",
        "$13.990", 2, Icons.Default.Spa
    ),
    ShopProduct(
        6, "Aceite corporal",
        "Ideal para el cuidado de la piel.",
        "$10.990", 2, Icons.Default.WaterDrop
    ),

    // Aromaterapia
    ShopProduct(
        7, "Aceite esencial",
        "Esencias para aromaterapia.",
        "$8.990", 3, Icons.Default.WaterDrop
    ),
    ShopProduct(
        8, "Difusor aromático",
        "Aromatiza tus espacios.",
        "$18.990", 3, Icons.Default.Spa
    ),

    // Bienestar
    ShopProduct(
        9, "Sales de baño",
        "Para complementar tus momentos de relajación.",
        "$8.990", 4, Icons.Default.Favorite
    ),
    ShopProduct(
        10, "Kit de bienestar",
        "Productos para tu rutina de relajación.",
        "$22.990", 4, Icons.Default.Favorite
    )
)

private val categoryNames = mapOf(
    1 to "Cuidado facial",
    2 to "Cuidado corporal",
    3 to "Aromaterapia",
    4 to "Bienestar"
)

@Composable
fun CategoryProductsScreen(
    categoryId: Int,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onProductSelected: (Int) -> Unit = {}
) {

    val categoryName = categoryNames[categoryId] ?: "Productos"

    val filteredProducts = shopProducts.filter {
        it.categoryId == categoryId
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FondoProductos)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
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
                    tint = VerdeProductos
                )
            }

            Text(
                text = categoryName,
                style = MaterialTheme.typography.headlineSmall,
                color = TextoProductos,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = "Productos disponibles.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        // Productos en dos columnas
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            filteredProducts.chunked(2).forEach { rowProducts ->

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    rowProducts.forEach { product ->

                        ProductCard(
                            product = product,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                onProductSelected(product.id)
                            }
                        )
                    }

                    if (rowProducts.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductCard(
    product: ShopProduct,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            // Información del producto
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {

                Text(
                    text = product.name,
                    color = TextoProductos,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = product.description,
                    color = Color.Gray,
                    fontSize = 10.sp,
                    lineHeight = 13.sp
                )

                Text(
                    text = product.price,
                    color = VerdeProductos,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Imagen representativa temporal
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFEEF1E9)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = product.icon,
                        contentDescription = null,
                        tint = VerdeProductos,
                        modifier = Modifier.size(25.dp)
                    )
                }
            }
        }
    }
}
