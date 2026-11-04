package com.sky.aegis.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

enum class AppTheme(val id: String, val displayName: String, val icon: String) {
    CYBER("cyber", "Cyber", "⚡"),
    LIGHT("light", "Light", "☀️"),
    SAKURA("sakura", "Sakura", "🌸")
}

data class AegisColorPalette(
    val background: Color,
    val surface: Color,
    val surfaceElevated: Color,
    val primary: Color,
    val secondary: Color,
    val accent: Color,
    val textPrimary: Color,
    val textMuted: Color,
    val border: Color,
    val isSakura: Boolean = false
)

val LocalAegisColors = staticCompositionLocalOf {
    AegisColorPalette(
        background = Obsidian950,
        surface = Obsidian900,
        surfaceElevated = Obsidian800,
        primary = CyberCyan,
        secondary = CyberMagenta,
        accent = CyberEmerald,
        textPrimary = TextPrimaryDark,
        textMuted = TextMutedDark,
        border = BorderSubtleDark
    )
}

@Composable
fun AegisTheme(
    theme: AppTheme = AppTheme.CYBER,
    content: @Composable () -> Unit
) {
    val colors = when (theme) {
        AppTheme.CYBER -> AegisColorPalette(
            background = Obsidian950,
            surface = Obsidian900,
            surfaceElevated = Obsidian800,
            primary = CyberCyan,
            secondary = CyberMagenta,
            accent = CyberEmerald,
            textPrimary = TextPrimaryDark,
            textMuted = TextMutedDark,
            border = BorderSubtleDark
        )
        AppTheme.SAKURA -> AegisColorPalette(
            background = FemboyBg,
            surface = FemboyCard,
            surfaceElevated = FemboyCardElevated,
            primary = FemboyPink,
            secondary = FemboyViolet,
            accent = FemboyPastel,
            textPrimary = FemboyTextPrimary,
            textMuted = FemboyTextMuted,
            border = FemboyBorder,
            isSakura = true
        )
        AppTheme.LIGHT -> AegisColorPalette(
            background = LightBg,
            surface = LightCard,
            surfaceElevated = LightCardElevated,
            primary = LightPrimary,
            secondary = LightSecondary,
            accent = LightAccent,
            textPrimary = LightTextPrimary,
            textMuted = LightTextMuted,
            border = LightBorder
        )
    }

    CompositionLocalProvider(LocalAegisColors provides colors) {
        MaterialTheme(
            colorScheme = if (theme == AppTheme.LIGHT) {
                lightColorScheme(
                    background = colors.background,
                    surface = colors.surface,
                    primary = colors.primary,
                    secondary = colors.secondary
                )
            } else {
                darkColorScheme(
                    background = colors.background,
                    surface = colors.surface,
                    primary = colors.primary,
                    secondary = colors.secondary
                )
            },
            content = content
        )
    }
}
