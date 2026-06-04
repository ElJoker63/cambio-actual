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
import java.util.*
import kotlinx.coroutines.flow.first

class QuickWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val database = AppDatabase.getInstance(context)
        val historyDao = database.rateDao()
        val themePrefs = ThemePreferences(context)
        
        val latest = historyDao.getLatestRateForCode("USD")
        val isDark = themePrefs.isDarkTheme.first()
        
        provideContent {
            QuickWidgetContent(latest, isDark)
        }
    }

    @Composable
    private fun QuickWidgetContent(
        latest: RateHistory?,
        isDark: Boolean
    ) {
        val primaryColor = if (isDark) Color(0xFF00B0FF) else Color(0xFF0061A4)
        val backgroundColor = if (isDark) Color.Black else Color.White
        val secondaryTextColor = if (isDark) Color.LightGray else Color.Gray

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(12.dp)
                .clickable(actionStartActivity<MainActivity>()),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    provider = ImageProvider(R.drawable.ic_usd),
                    contentDescription = "USD",
                    modifier = GlanceModifier.size(56.dp) // Larger icon for 2x2
                )
                Spacer(modifier = GlanceModifier.height(12.dp))
                Text(
                    text = "USD",
                    style = TextStyle(
                        color = ColorProvider(secondaryTextColor),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = String.format(Locale.US, "$%.2f", latest?.value ?: 0.0),
                    style = TextStyle(
                        color = ColorProvider(primaryColor),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}
