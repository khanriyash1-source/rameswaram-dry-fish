package com.rameswaram.dryfish.ui.theme

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import kotlinx.coroutines.delay

// Predefined animation values for consistent feel
object AnimationValues {
    // Scale animations
    const val scaleSmall = 0.85f
    const val scaleNormal = 1.0f
    const val scaleLarge = 1.05f
    const val scalePressed = 0.95f
    
    // Alpha animations
    const val alphaInvisible = 0f
    const val alphaDim = 0.5f
    const val alphaVisible = 1f
    
    // Elevation (in dp)
    const val elevationNone = 0
    const val elevationSmall = 2
    const val elevationMedium = 4
    const val elevationLarge = 8
    const val elevationXLarge = 16
    
    // Translation (in dp)
    const val translateSmall = 4
    const val translateMedium = 8
    const val translateLarge = 16
}

// Custom tween specs for different UI elements
object CustomEasing {
    val emphasize = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)
    val standard = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)
    val decelerate = CubicBezierEasing(0.0f, 0.0f, 0.2f, 1.0f)
    val accelerate = CubicBezierEasing(0.4f, 0.0f, 1.0f, 1.0f)
}

// Entrance animations for lists
@Composable
fun <T> rememberStaggeredAnimation(
    items: List<T>,
    delayMillis: Long = 100L
): Map<T, Float> {
    val animatedValues = remember(items) {
        items.mapIndexed { index, item ->
            item to Animatable(0f)
        }.toMap()
    }
    
    items.forEachIndexed { index, item ->
        val animatable = animatedValues[item] ?: return@forEachIndexed
        LaunchedEffect(item) {
            delay(index * delayMillis)
            animatable.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        }
    }
    
    return animatedValues.mapValues { it.value.value }
}

// Float extension for animations
fun Float.animateEnter(): EnterTransition {
    return fadeIn(
        animationSpec = tween(300),
        initialAlpha = this
    ) + scaleIn(
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        initialScale = AnimationValues.scaleSmall
    )
}

// Haptic feedback helper (requires context)
fun triggerHapticFeedback(type: HapticFeedbackType) {
    // Implementation in UI layer with LocalHapticFeedback
}

// Reveal animation for cards
fun cardRevealAnimation(delay: Int = 0): EnterTransition {
    return fadeIn(
        animationSpec = tween(
            durationMillis = 400,
            delayMillis = delay,
            easing = CustomEasing.decelerate
        )
    ) + scaleIn(
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        initialScale = 0.8f
    )
}

// Slide up animation
fun slideUpAnimation(delay: Int = 0): EnterTransition {
    return slideInVertically(
        animationSpec = tween(
            durationMillis = 400,
            delayMillis = delay,
            easing = CustomEasing.decelerate
        ),
        initialOffsetY = { it / 2 }
    ) + fadeIn(
        animationSpec = tween(400, delayMillis = delay)
    )
}
