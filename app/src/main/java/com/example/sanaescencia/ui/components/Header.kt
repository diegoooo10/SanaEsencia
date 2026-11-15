
package com.example.sanaescencia.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.sanaescencia.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Header() {
    TopAppBar(
        title = {
            Image(
                painter = painterResource(R.drawable.logo_header),
                contentDescription = "Logo de Sanaesencia",
                modifier = Modifier.height(48.dp),
                contentScale = ContentScale.Fit
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}
