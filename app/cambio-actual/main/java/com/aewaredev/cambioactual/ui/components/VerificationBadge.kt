package com.aewaredev.cambioactual.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.Pending
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun VerificationBadge(status: String, showText: Boolean = false) {
    val (icon, color, text) = when (status) {
        "approved" -> Triple(Icons.Rounded.CheckCircle, Color(0xFF4CAF50), "Verificado")
        "pending", "manual_review" -> Triple(Icons.Rounded.Pending, Color(0xFFFFC107), "En revisión")
        "rejected" -> Triple(Icons.Rounded.Error, Color(0xFFF44336), "Rechazado")
        else -> Triple(null, Color.Gray, "No verificado")
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = text,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
        }
        if (showText) {
            Text(
                text = text,
                color = color,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}
