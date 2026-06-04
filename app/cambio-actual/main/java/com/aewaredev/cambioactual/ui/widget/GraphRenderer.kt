package com.aewaredev.cambioactual.ui.widget

import android.graphics.*
import com.aewaredev.cambioactual.data.model.RateHistory

object GraphRenderer {
    fun renderGraph(
        history: List<RateHistory>,
        width: Int,
        height: Int,
        isDark: Boolean
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        
        if (history.size < 2) return bitmap

        val sortedHistory = history.sortedBy { it.timestamp }
        val minRate = sortedHistory.minOf { it.value }
        val maxRate = sortedHistory.maxOf { it.value }
        val rawRange = maxRate - minRate
        
        // Mantain 15% vertical padding
        val verticalPadding = if (rawRange == 0.0) 10.0 else rawRange * 0.15
        val minY = minRate - verticalPadding
        val maxY = maxRate + verticalPadding
        val range = (maxY - minY).coerceAtLeast(1.0)

        val points = sortedHistory.mapIndexed { index, rate ->
            val x = index.toFloat() * (width.toFloat() / (sortedHistory.size - 1))
            val y = height.toFloat() - ((rate.value - minY) / range * height).toFloat()
            PointF(x, y)
        }

        val primaryColor = if (isDark) 0xFF00B0FF.toInt() else 0xFF0061A4.toInt()
        
        // Path for fill - More vibrant gradient
        val fillPath = Path()
        fillPath.moveTo(points[0].x, points[0].y)
        for (i in 1 until points.size) {
            fillPath.lineTo(points[i].x, points[i].y)
        }
        fillPath.lineTo(width.toFloat(), height.toFloat())
        fillPath.lineTo(0f, height.toFloat())
        fillPath.close()

        val fillPaint = Paint().apply {
            shader = LinearGradient(
                0f, 0f, 0f, height.toFloat(),
                intArrayOf(primaryColor and 0x66FFFFFF, primaryColor and 0x22FFFFFF, Color.TRANSPARENT),
                floatArrayOf(0f, 0.5f, 1f),
                Shader.TileMode.CLAMP
            )
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        canvas.drawPath(fillPath, fillPaint)

        // Path for stroke - Thicker and smoother (Rounded)
        val strokePath = Path()
        strokePath.moveTo(points[0].x, points[0].y)
        for (i in 1 until points.size) {
            strokePath.lineTo(points[i].x, points[i].y)
        }

        val strokePaint = Paint().apply {
            color = primaryColor
            strokeWidth = 6f // Thicker line
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND // Smoother tips
            strokeJoin = Paint.Join.ROUND // Smoother joints
            isAntiAlias = true
            // Add a subtle outer glow if in dark mode
            if (isDark) {
                maskFilter = BlurMaskFilter(2f, BlurMaskFilter.Blur.NORMAL)
            }
        }
        canvas.drawPath(strokePath, strokePaint)
        
        // Draw the main line on top of the glow if was added
        if (isDark) {
            strokePaint.maskFilter = null
            canvas.drawPath(strokePath, strokePaint)
        }

        return bitmap
    }
}
