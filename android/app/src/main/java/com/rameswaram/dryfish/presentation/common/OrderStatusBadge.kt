package com.rameswaram.dryfish.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rameswaram.dryfish.domain.model.OrderStatus
import com.rameswaram.dryfish.ui.theme.*

@Composable
fun OrderStatusBadge(
    status: OrderStatus,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = when (status) {
        OrderStatus.PENDING -> SunsetOrangeLight to SunsetOrangeDark
        OrderStatus.CONFIRMED -> OceanBlueLight to OceanBlueDark
        OrderStatus.PROCESSING -> Color(0xFFE3F2FD) to Info
        OrderStatus.SHIPPED -> Color(0xFFF3E5F5) to DeepNavy
        OrderStatus.DELIVERED -> Color(0xFFE8F5E9) to Success
        OrderStatus.CANCELLED -> Color(0xFFFFEBEE) to Error
        OrderStatus.RETURNED -> Color(0xFFFFF3E0) to Warning
    }

    Text(
        text = status.displayName(),
        color = textColor,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(backgroundColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}
