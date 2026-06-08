package com.romit.securebox.components.app

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun GradientBackground() {
    val color = MaterialTheme.colorScheme.primary
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .blur(150.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .size(275.dp)
        ) {
            drawCircle(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF8BC34A).copy(0.5f),
                        color.copy(alpha = 0.5f)
                    ),
                    start = Offset(
                        center.x - 275f,
                        center.y - 275f
                    ),
                    end = Offset(
                        center.x + 275f,
                        center.y + 275f
                    )
                ),
                radius = 500f,
                center = center
            )
        }
    }
}
