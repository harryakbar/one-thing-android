package dev.harryakbar.onething.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Brand palette
val Background = Color(0xFF0D0D0D)
val Accent = Color(0xFFE8A838)
val TextPrimary = Color(0xFFFAFAFA)
val TextSecondary = Color(0xFF888888)
val Surface = Color(0xFF1A1A1A)
val OutlineColor = Color(0xFF333333)

private val DarkColorScheme = darkColorScheme(
    primary = Accent,
    onPrimary = Color(0xFF0D0D0D),
    background = Background,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    secondary = TextSecondary,
    outline = OutlineColor
)

private val OneThingTypography = Typography(
    displayLarge = TextStyle(
        fontWeight = FontWeight.Black,
        fontSize = 72.sp,
        lineHeight = 76.sp,
        letterSpacing = (-2).sp,
        color = TextPrimary
    ),
    displayMedium = TextStyle(
        fontWeight = FontWeight.ExtraBold,
        fontSize = 48.sp,
        lineHeight = 52.sp,
        letterSpacing = (-1).sp,
        color = TextPrimary
    ),
    displaySmall = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 40.sp,
        color = TextPrimary
    ),
    headlineLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
        color = TextPrimary
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        color = TextPrimary
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        letterSpacing = 1.sp,
        color = TextPrimary
    )
)

@Composable
fun OneThingTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = OneThingTypography,
        content = content
    )
}
