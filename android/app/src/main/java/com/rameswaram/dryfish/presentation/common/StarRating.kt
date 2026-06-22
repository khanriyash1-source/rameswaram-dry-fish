package com.rameswaram.dryfish.presentation.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rameswaram.dryfish.ui.theme.SunsetOrange

@Composable
fun StarRating(
    rating: Double,
    reviewCount: Int = 0,
    modifier: Modifier = Modifier,
    showCount: Boolean = true,
    starSize: Int = 14
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val fullStars = rating.toInt()
        val hasHalf = rating - fullStars >= 0.5
        val emptyStars = 5 - fullStars - (if (hasHalf) 1 else 0)

        repeat(fullStars) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Star",
                tint = SunsetOrange,
                modifier = Modifier.size(starSize.dp)
            )
        }

        if (hasHalf) {
            Icon(
                imageVector = Icons.Default.StarHalf,
                contentDescription = "Half Star",
                tint = SunsetOrange,
                modifier = Modifier.size(starSize.dp)
            )
        }

        repeat(emptyStars) {
            Icon(
                imageVector = Icons.Default.StarOutline,
                contentDescription = "Empty Star",
                tint = SunsetOrange,
                modifier = Modifier.size(starSize.dp)
            )
        }

        if (showCount && reviewCount > 0) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "($reviewCount)",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
