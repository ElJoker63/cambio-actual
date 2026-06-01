package com.aewaredev.cambioactual.ui.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.*
import androidx.glance.layout.*
import androidx.glance.text.*
import androidx.glance.unit.ColorProvider
import com.aewaredev.cambioactual.MainActivity
import com.aewaredev.cambioactual.R
import com.aewaredev.cambioactual.data.local.AppDatabase
import com.aewaredev.cambioactual.data.model.RateHistory
import com.aewaredev.cambioactual.data.preferences.ThemePreferences
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.flow.first

class ExchangeWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val database = AppDatabase.getInstance(context)
        val historyDao = database.rateDao()
        val themePrefs = ThemePreferences(context)
        
        // Fetch full history available (TODO range) for the graph
        val fullHistory = historyDao.getHistoryForCode("USD").first()
        
        // Fetch last 2 days for the labels
        val twoDaysAgo = System.currentTimeMillis() - (2 * 24 * 60 * 60 * 1000L)
        val labelsHistory = historyDao.getHistoryForCodeSince("USD", twoDaysAgo).first()
        
        val isDark = themePrefs.isDarkTheme.first()
        
        provideContent {
            ExchangeWidgetContent(fullHistory, labelsHistory, isDark)
        }
    }

    @Composable
    private fun ExchangeWidgetContent(
        fullHistory: List<RateHistory>,
        labelsHistory: List<RateHistory>,
        isDark: Boolean
    ) {
        // Labels use last 2 days data
        val latest = labelsHistory.lastOrNull()
        val startLabel = labelsHistory.firstOrNull()
        
        val primaryColor = if (isDark) Color(0xFF00B0FF) else Color(0xFF0061A4)
        val backgroundColor = if (isDark) Color.Black else Color.White
        val textColor = if (isDark) Color.White else Color.Black
        val secondaryTextColor = if (isDark) Color.LightGray else Color.Gray
        
        val size = LocalSize.current

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(12.dp)
                .clickable(actionStartActivity<MainActivity>()),
            verticalAlignment = Alignment.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    provider = ImageProvider(R.drawable.ic_usd),
                    contentDescription = "USD",
                    modifier = GlanceModifier.size(24.dp)
                )
                Spacer(modifier = GlanceModifier.width(8.dp))
                Text(
                    text = "USD",
                    style = TextStyle(
                        color = ColorProvider(textColor),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = GlanceModifier.defaultWeight())
            }

            Spacer(modifier = GlanceModifier.defaultWeight())

            // Graph - Shows Full History
            if (fullHistory.size >= 2) {
                val density = 3f
                val bitmapWidth = (size.width.value * density).toInt().coerceAtLeast(100)
                val bitmapHeight = (size.height.value * 0.4f * density).toInt().coerceAtLeast(50)
                
                val bitmap = GraphRenderer.renderGraph(fullHistory, bitmapWidth, bitmapHeight, isDark)
                Image(
                    provider = ImageProvider(bitmap),
                    contentDescription = "Trend Graph",
                    modifier = GlanceModifier.fillMaxWidth().height(80.dp)
                )
            } else {
                Box(modifier = GlanceModifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) {
                    Text("Sin datos", style = TextStyle(color = ColorProvider(secondaryTextColor)))
                }
            }

            Spacer(modifier = GlanceModifier.defaultWeight())

            // Bottom Info - Bottom left (2 days ago), Bottom right (current)
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = String.format(Locale.US, "$%.2f", startLabel?.value ?: 0.0),
                        style = TextStyle(
                            color = ColorProvider(textColor), 
                            fontSize = 16.sp, 
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = formatDate(startLabel?.timestamp),
                        style = TextStyle(
                            color = ColorProvider(secondaryTextColor),
                            fontSize = 10.sp
                        )
                    )
                }
                Spacer(modifier = GlanceModifier.defaultWeight())
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = String.format(Locale.US, "$%.2f", latest?.value ?: 0.0),
                        style = TextStyle(
                            color = ColorProvider(primaryColor), 
                            fontSize = 20.sp, 
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = formatDateTime(latest?.timestamp),
                        style = TextStyle(
                            color = ColorProvider(secondaryTextColor), 
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }
    }

    private fun formatDate(timestamp: Long?): String {
        if (timestamp == null) return ""
        val sdf = SimpleDateFormat("MMM d", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    private fun formatDateTime(timestamp: Long?): String {
        if (timestamp == null) return ""
        val sdf = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
