package com.rameswaram.dryfish.presentation.common

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun CustomDrawerLayout(
    isOpen: Boolean,
    onOpenChanged: (Boolean) -> Unit,
    gesturesEnabled: Boolean = true,
    drawerContent: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    val anim = remember { Animatable(if (isOpen) 1f else 0f) }
    val density = LocalDensity.current
    val drawerWidthPx = with(density) { 280.dp.toPx() }

    LaunchedEffect(isOpen) {
        if (isOpen) anim.animateTo(1f, tween(250))
        else anim.animateTo(0f, tween(200))
    }

    val progress = anim.value

    Box(Modifier.fillMaxSize().clipToBounds()) {
        Box(
            Modifier
                .offset { IntOffset((progress * drawerWidthPx).roundToInt(), 0) }
                .fillMaxSize()
        ) {
            content()
        }

        if (progress > 0f) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = progress * 0.5f))
                    .clickable { onOpenChanged(false) }
            )
        }

        // Drawer content with swipe-to-close
        Box(
            Modifier
                .width(280.dp)
                .fillMaxHeight()
                .offset { IntOffset(((progress - 1f) * drawerWidthPx).roundToInt(), 0) }
                .pointerInput(gesturesEnabled) {
                    if (gesturesEnabled) {
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                if (anim.value > 0.4f) {
                                    scope.launch { anim.animateTo(1f); onOpenChanged(true) }
                                } else {
                                    scope.launch { anim.animateTo(0f); onOpenChanged(false) }
                                }
                            },
                            onHorizontalDrag = { _, dragAmount ->
                                val newPos = (anim.value + dragAmount / drawerWidthPx).coerceIn(0f, 1f)
                                scope.launch { anim.snapTo(newPos) }
                            }
                        )
                    }
                }
        ) {
            drawerContent()
        }

        // Left edge swipe zone — wider, always active for opening/continuing drag
        if (gesturesEnabled) {
            Box(
                Modifier
                    .width(40.dp)
                    .fillMaxHeight()
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                if (anim.value > 0.4f) {
                                    scope.launch { anim.animateTo(1f); onOpenChanged(true) }
                                } else {
                                    scope.launch { anim.animateTo(0f); onOpenChanged(false) }
                                }
                            },
                            onHorizontalDrag = { _, dragAmount ->
                                val newPos = (anim.value + dragAmount / drawerWidthPx).coerceIn(0f, 1f)
                                scope.launch { anim.snapTo(newPos) }
                            }
                        )
                    }
            )
        }
    }
}
