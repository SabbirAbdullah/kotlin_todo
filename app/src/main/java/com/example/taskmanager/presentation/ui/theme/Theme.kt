package com.example.taskmanager.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme


// ── Brand Colors ──────────────────────────────────────────────────────────────
val Purple = Color(0xFF7C5CBF)
val PurpleLight = Color(0xFF9C7FD4)
val PurpleDark = Color(0xFF5A3E9A)
val Teal = Color(0xFF00BFA5)
val PendingOrange = Color(0xFFFF9800)
val CompletedGreen = Color(0xFF4CAF50)
val DeleteRed = Color(0xFFE53935)
val CardBg = Color(0xFFF8F6FF)
val DarkCardBg = Color(0xFF2A2640)
val DarkBg = Color(0xFF1A1730)
val DarkSurface = Color(0xFF221F3D)

// ── Color Schemes ─────────────────────────────────────────────────────────────
private val LightColorScheme = lightColorScheme(
    primary = Purple,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEDE7FF),
    onPrimaryContainer = PurpleDark,
    secondary = Teal,
    onSecondary = Color.White,
    background = Color(0xFFF5F3FF),
    onBackground = Color(0xFF1C1B22),
    surface = Color.White,
    onSurface = Color(0xFF1C1B22),
    surfaceVariant = Color(0xFFF0EDF8),
    error = DeleteRed,
    outline = Color(0xFFCBC8D4)
)

private val DarkColorScheme = darkColorScheme(
    primary = PurpleLight,
    onPrimary = Color.White,
    primaryContainer = PurpleDark,
    onPrimaryContainer = Color(0xFFE8DEFF),
    secondary = Teal,
    onSecondary = Color.Black,
    background = DarkBg,
    onBackground = Color(0xFFE6E1F5),
    surface = DarkSurface,
    onSurface = Color(0xFFE6E1F5),
    surfaceVariant = DarkCardBg,
    error = Color(0xFFCF6679),
    outline = Color(0xFF4A4660)
)

// ── Typography ─────────────────────────────────────────────────────────────────
val AppTypography = Typography(
    headlineLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 28.sp, lineHeight = 36.sp),
    headlineMedium = TextStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 32.sp),
    headlineSmall = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 20.sp, lineHeight = 28.sp),
    titleLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 18.sp, lineHeight = 26.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 24.sp),
    bodyLarge = TextStyle(fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontSize = 12.sp, lineHeight = 16.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp),
    labelSmall = TextStyle(fontWeight = FontWeight.Medium, fontSize = 11.sp)
)

// ── Theme ─────────────────────────────────────────────────────────────────────
@Composable
fun TaskManagerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}