package com.rameswaram.dryfish.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.animation.core.*
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Animation Specs
object AnimationSpecs {
    // Spring animations for bouncy feel
    val springFast = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    )
    
    val springSlow = spring<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessLow
    )
    
    // Tween for smooth transitions
    val tweenFast = tween<Float>(
        durationMillis = 200,
        easing = FastOutSlowInEasing
    )
    
    val tweenMedium = tween<Float>(
        durationMillis = 300,
        easing = FastOutSlowInEasing
    )
    
    val tweenSlow = tween<Float>(
        durationMillis = 500,
        easing = LinearOutSlowInEasing
    )
    
    // Stagger delays for lists
    const val staggerDelay = 50L
    
    // Shimmer duration
    const val shimmerDuration = 1500
}

// Extended Color Scheme with our brand colors
private val LightColorScheme = lightColorScheme(
    primary = OceanBlue,
    onPrimary = Color.White,
    primaryContainer = OceanBlueLight,
    onPrimaryContainer = OceanBlueDark,
    secondary = DeepNavy,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE8EAF6),
    onSecondaryContainer = DeepNavyDark,
    tertiary = SunsetOrange,
    onTertiary = Color.White,
    tertiaryContainer = SunsetOrangeLight,
    onTertiaryContainer = SunsetOrangeDark,
    background = LightBackground,
    onBackground = LightOnSurface,
    surface = Color.White,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    outlineVariant = Color(0xFFCAC4D0),
    error = Error,
    onError = Color.White,
    errorContainer = ErrorLight,
    onErrorContainer = Error
)

private val DarkColorScheme = darkColorScheme(
    primary = OceanBlue,
    onPrimary = Color.White,
    primaryContainer = OceanBlueDark,
    onPrimaryContainer = OceanBlueLight,
    secondary = DeepNavyLight,
    onSecondary = Color.White,
    secondaryContainer = DeepNavyDark,
    onSecondaryContainer = Color(0xFFE8EAF6),
    tertiary = SunsetOrange,
    onTertiary = Color.White,
    tertiaryContainer = SunsetOrangeDark,
    onTertiaryContainer = SunsetOrangeLight,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    outlineVariant = Color(0xFF49454F),
    error = Error,
    onError = Color.White,
    errorContainer = Color(0xFF5C1818),
    onErrorContainer = Color(0xFFFFCDD2)
)

// Theme State Holder
object ThemeState {
    val isDarkMode = booleanPreferencesKey("is_dark_mode")
    val useSystemTheme = booleanPreferencesKey("use_system_theme")
}

@Composable
fun RameswaramTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = androidx.compose.ui.platform.LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            window.navigationBarColor = android.graphics.Color.TRANSPARENT
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    CompositionLocalProvider(
        LocalAnimationSpec provides AnimationSpecs
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

// Composition Local for animation specs
val LocalAnimationSpec = compositionLocalOf { AnimationSpecs }
