package com.example.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun WeatherGradientBackground(
    condition: String?,
    isDarkMode: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val gradientColors = when {
        isDarkMode -> listOf(
            Color(0xFF0F2027),
            Color(0xFF203A43),
            Color(0xFF2C5364)
        )
        condition?.contains("clear", ignoreCase = true) == true || condition?.contains("sun", ignoreCase = true) == true -> listOf(
            Color(0xFF2980B9),
            Color(0xFF6DD5FA),
            Color(0xFFFFFFFF)
        )
        condition?.contains("rain", ignoreCase = true) == true || condition?.contains("drizzle", ignoreCase = true) == true -> listOf(
            Color(0xFF373B44),
            Color(0xFF4286F4)
        )
        condition?.contains("cloud", ignoreCase = true) == true -> listOf(
            Color(0xFF616161),
            Color(0xFF9BC5C3)
        )
        condition?.contains("snow", ignoreCase = true) == true -> listOf(
            Color(0xFFE6DADA),
            Color(0xFF274046)
        )
        else -> listOf(
            Color(0xFF3A6073),
            Color(0xFF16222A)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gradientColors)),
        content = content
    )
}
