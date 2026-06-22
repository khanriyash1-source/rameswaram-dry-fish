package com.rameswaram.dryfish.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

// Modern shape system
object Shapes {
    // Extra small - chips, small buttons
    val extraSmall = RoundedCornerShape(4.dp)
    
    // Small - input fields, small cards
    val small = RoundedCornerShape(8.dp)
    
    // Medium - standard cards
    val medium = RoundedCornerShape(12.dp)
    
    // Large - modals, dialogs
    val large = RoundedCornerShape(16.dp)
    
    // Extra large - bottom sheets, hero cards
    val extraLarge = RoundedCornerShape(24.dp)
    
    // Full - circular buttons, avatars
    val full = RoundedCornerShape(50)
    
    // Top rounded only (for bottom sheets)
    val topRounded = RoundedCornerShape(
        topStart = 24.dp,
        topEnd = 24.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    )
    
    // Asymmetric - modern card design
    val asymmetric = RoundedCornerShape(
        topStart = 20.dp,
        topEnd = 8.dp,
        bottomStart = 8.dp,
        bottomEnd = 20.dp
    )
}

// Custom wave shape for hero section
class WaveShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            val width = size.width
            val height = size.height
            
            moveTo(0f, height * 0.7f)
            
            // First wave
            quadraticBezierTo(
                width * 0.25f, height * 0.5f,
                width * 0.5f, height * 0.7f
            )
            
            // Second wave
            quadraticBezierTo(
                width * 0.75f, height * 0.9f,
                width, height * 0.7f
            )
            
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }
        
        return Outline.Generic(path)
    }
}

// Ticket-style shape with cut corners
class TicketShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val cornerSize = with(density) { 20.dp.toPx() }
        val path = Path().apply {
            val width = size.width
            val height = size.height
            
            // Start from top-left after corner
            moveTo(cornerSize, 0f)
            
            // Top edge
            lineTo(width - cornerSize, 0f)
            
            // Top-right corner
            quadraticBezierTo(width, 0f, width, cornerSize)
            
            // Right edge
            lineTo(width, height - cornerSize)
            
            // Bottom-right corner
            quadraticBezierTo(width, height, width - cornerSize, height)
            
            // Bottom edge with cut-out
            lineTo(width * 0.6f, height)
            lineTo(width * 0.55f, height + cornerSize * 0.5f)
            lineTo(width * 0.5f, height)
            lineTo(width * 0.45f, height - cornerSize * 0.5f)
            lineTo(width * 0.4f, height)
            lineTo(cornerSize, height)
            
            // Bottom-left corner
            quadraticBezierTo(0f, height, 0f, height - cornerSize)
            
            // Left edge
            lineTo(0f, cornerSize)
            
            // Top-left corner
            quadraticBezierTo(0f, 0f, cornerSize, 0f)
            
            close()
        }
        
        return Outline.Generic(path)
    }
}