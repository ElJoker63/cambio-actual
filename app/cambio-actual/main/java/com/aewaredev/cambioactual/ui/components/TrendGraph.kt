package com.aewaredev.cambioactual.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aewaredev.cambioactual.data.model.RateHistory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

@Composable
fun TrendGraph(
    history: List<RateHistory>,
    currentPrice: Double,
    currencyCode: String,
    selectedPeriod: String,
    onPeriodSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var touchX by remember { mutableStateOf<Float?>(null) }
    var touchIndex by remember { mutableStateOf<Int?>(null) }
    val periods = listOf("1D", "1S", "3S", "3M", "6M", "TODO")
    
    val displayHistory = remember(history) { history.sortedBy { it.timestamp } }
    
    val (headerValue, headerDate) = remember(touchIndex, displayHistory, currentPrice) {
        if (displayHistory.isEmpty()) return@remember Pair("---", "---")
        
        if (touchIndex == null) {
            Pair(
                String.format(Locale.US, "%.2f", currentPrice),
                "Últimos datos"
            )
        } else {
            val point = displayHistory.getOrNull(touchIndex!!) ?: displayHistory.last()
            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("es", "ES"))
            Pair(
                String.format(Locale.US, "%.2f", point.value),
                dateFormat.format(Date(point.timestamp))
            )
        }
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    Column(modifier = modifier.fillMaxWidth()) {
        TrendHeader(
            code = currencyCode,
            value = headerValue,
            date = headerDate
        )
        
        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(displayHistory) {
                        detectDragGestures(
                            onDragStart = { offset -> touchX = offset.x },
                            onDrag = { change, _ -> touchX = change.position.x },
                            onDragEnd = { touchX = null; touchIndex = null },
                            onDragCancel = { touchX = null; touchIndex = null }
                        )
                    }
            ) {
                if (displayHistory.size < 2) return@Canvas
                
                val width = size.width
                val height = size.height
                
                // 1. Improved Scaling with Vertical Padding (15% top/bottom)
                val rawMin = displayHistory.minOf { it.value }
                val rawMax = displayHistory.maxOf { it.value }
                val rawRange = rawMax - rawMin
                
                val verticalPadding = if (rawRange == 0.0) 10.0 else rawRange * 0.15
                val minRate = rawMin - verticalPadding
                val maxRate = rawMax + verticalPadding
                val range = (maxRate - minRate).coerceAtLeast(1.0)

                val spacing = width / (displayHistory.size - 1)

                val points = displayHistory.mapIndexed { index, rate ->
                    val x = index * spacing
                    val y = height - ((rate.value - minRate) / range * height).toFloat()
                    Offset(x, y)
                }

                // 2. Accurate Touch Point Detection
                touchX?.let { x ->
                    // Find the index of the point whose X coordinate is closest to touchX
                    val closestIndex = points.indices.minByOrNull { abs(points[it].x - x) }
                    if (closestIndex != null) {
                        touchIndex = closestIndex
                        val point = points[closestIndex]
                        
                        // Vertical dashed line
                        drawLine(
                            color = onSurfaceColor.copy(alpha = 0.3f),
                            start = Offset(point.x, 0f),
                            end = Offset(point.x, height),
                            strokeWidth = 1.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                        )

                        // Intersection Marker
                        drawCircle(
                            color = Color.White,
                            radius = 6.dp.toPx(),
                            center = point
                        )
                        drawCircle(
                            color = primaryColor,
                            radius = 4.dp.toPx(),
                            center = point
                        )
                    }
                }

                val strokePath = Path().apply {
                    moveTo(points[0].x, points[0].y)
                    for (i in 1 until points.size) {
                        lineTo(points[i].x, points[i].y)
                    }
                }

                val fillPath = Path().apply {
                    addPath(strokePath)
                    lineTo(width, height)
                    lineTo(0f, height)
                    close()
                }

                // Shaded Area
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    )
                )

                // Smooth Trend Line
                drawPath(
                    path = strokePath,
                    color = primaryColor,
                    style = Stroke(
                        width = 3.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            periods.forEach { period ->
                PeriodButton(
                    label = period,
                    isSelected = period == selectedPeriod,
                    onClick = { onPeriodSelected(period) }
                )
            }
        }
    }
}

@Composable
fun TrendHeader(
    code: String,
    value: String,
    date: String
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text(
                    text = code,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Black
                )
            }
            Text(
                text = date,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
    }
}

@Composable
fun PeriodButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .width(48.dp)
            .height(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) primaryColor.copy(alpha = 0.15f) else Color.Transparent),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) primaryColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
