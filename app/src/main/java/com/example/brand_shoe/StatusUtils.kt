package com.example.brand_shoe

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

fun statusColor(status: String): Color {
    return when (status) {
        "Pending" -> Color(0xFFFFA000)
        "Shipped" -> Color(0xFF1976D2)
        "Delivered" -> Color(0xFF2E7D32)
        "Cancelled" -> Color(0xFFE53935)
        else -> Color(0xFF757575)
    }
}

@Composable
fun StatusBadge(status: String) {
    val color = statusColor(status)
    Surface(
        shape = RoundedCornerShape(50),
        color = color.copy(alpha = 0.14f),
        contentColor = color
    ) {
        Text(
            text = status,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}