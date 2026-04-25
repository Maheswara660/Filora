package com.maheswara660.filora.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.maheswara660.filora.App.Companion.globalClass
import com.maheswara660.filora.screen.preferences.constant.ThemePreference

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = LightOnSecondaryContainer,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface
)

private val AmoledColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    background = Color.Black,
    onBackground = DarkOnBackground,
    surface = Color.Black,
    onSurface = DarkOnSurface,
    surfaceVariant = Color(0xFF121212),
    onSurfaceVariant = DarkOnSurface
)

@Composable
fun FiloraTheme(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val manager = globalClass.preferencesManager
    
    val isSystemDark = isSystemInDarkTheme()
    val themePref = manager.theme
    
    val darkTheme: Boolean = when (themePref) {
        ThemePreference.LIGHT.ordinal -> false
        ThemePreference.DARK.ordinal -> true
        ThemePreference.AMOLED.ordinal -> true
        else -> isSystemDark
    }

    val colorScheme = remember(themePref, manager.accentColor, isSystemDark) {
        val baseScheme = when (themePref) {
            ThemePreference.LIGHT.ordinal -> LightColorScheme
            ThemePreference.DARK.ordinal -> DarkColorScheme
            ThemePreference.AMOLED.ordinal -> AmoledColorScheme
            else -> if (isSystemDark) DarkColorScheme else LightColorScheme
        }

        // Apply Accents
        val schemeWithAccent = if (manager.accentColor == 1 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Material You
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        } else if (manager.accentColor >= 2) {
            // Custom Accents
            val accent = CustomAccents.getOrNull(manager.accentColor - 2)
            if (accent != null) {
                baseScheme.copy(
                    primary = accent.primary,
                    secondary = accent.secondary,
                    outline = accent.primary.copy(alpha = 0.5f)
                )
            } else baseScheme
        } else {
            // Default (Teal)
            baseScheme
        }

        // Special handling for AMOLED surfaces if Material You is used in AMOLED theme
        if (themePref == ThemePreference.AMOLED.ordinal) {
            schemeWithAccent.copy(
                surface = Color.Black,
                background = Color.Black,
                surfaceContainer = Color.Black,
                surfaceContainerHigh = Color(0xFF121212),
                surfaceContainerLow = Color.Black
            )
        } else {
            schemeWithAccent
        }
    }

    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            (view.context as? Activity)?.window?.let {
                WindowCompat.getInsetsController(it, view).isAppearanceLightStatusBars = !darkTheme
                @Suppress("DEPRECATION")
                it.statusBarColor = Color.Transparent.toArgb()
                @Suppress("DEPRECATION")
                it.navigationBarColor = Color.Transparent.toArgb()
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}