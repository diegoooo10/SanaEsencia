
package com.example.sanaescencia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

private val VerdeTienda = Color(0xFF617A56)
private val FondoTienda = Color(0xFFF9F8F6)
private val TextoTienda = Color(0xFF344B35)

private data class ShopCategory(
    val id: Int,
    val name: String,
    val description: String
)

private val categories = listOf(
    ShopCategory(
        id = 1,
        name = "Cuidado facial",
        description = "Productos para el cuidado de tu piel."
    ),
    ShopCategory(
        id = 2,
        name = "Cuidado corporal",
        description = "Productos para el cuidado corporal."
    ),
    ShopCategory(
        id = 3,
        name = "Aromaterapia",
        description = "Esencias y productos aromáticos."
    ),
    ShopCategory(
        id = 4,
        name = "Bienestar",
        description = "Productos para tu bienestar."
    )
)

@Composable
fun ShopScreen(
    modifier: Modifier = Modifier,
    onCategorySelected: (Int) -> Unit = {}
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FondoTienda)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Encabezado
        Text(
            text = "Tienda",
            style = MaterialTheme.typography.headlineMedium,
            color = TextoTienda,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Explora nuestros productos y encuentra lo que necesitas.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        // Categorías
        Text(
            text = "Categorías",
            style = MaterialTheme.typography.titleMedium,
            color = VerdeTienda,
            fontWeight = FontWeight.SemiBold
        )

        // Botones en dos columnas
        categories.chunked(2).forEach { rowCategories ->

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                rowCategories.forEach { category ->

                    Button(
                        onClick = {
                            onCategorySelected(category.id)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(76.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VerdeTienda
                        ),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        Text(
                            text = category.name,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                // Mantiene dos columnas si queda una categoría
                if (rowCategories.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
