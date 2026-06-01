package com.aewaredev.cambioactual.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aewaredev.cambioactual.R
import com.aewaredev.cambioactual.data.model.ExchangeRate
import com.aewaredev.cambioactual.ui.components.TrendGraph
import com.aewaredev.cambioactual.ui.viewmodel.ExchangeViewModel
import java.util.Locale

@Composable
fun MarketScreen(
    viewModel: ExchangeViewModel,
    modifier: Modifier = Modifier
) {
    val rates by viewModel.informalRates.collectAsState()
    val historyData by viewModel.historyData.collectAsState()
    val selectedCurrency by viewModel.selectedCurrency.collectAsState()
    val selectedPeriod by viewModel.selectedPeriod.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    
    val currentRate = rates.find { it.code == selectedCurrency }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        SimpleHeader(
            title = "CAMBIO ACTUAL",
            isDarkTheme = isDarkTheme,
            onThemeToggle = { viewModel.toggleTheme() }
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
        ) {
            item {
                TrendGraph(
                    history = historyData,
                    currentPrice = currentRate?.median ?: 0.0,
                    currencyCode = selectedCurrency,
                    selectedPeriod = selectedPeriod,
                    onPeriodSelected = { viewModel.selectPeriod(it) },
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            items(rates) { rate ->
                MarketRateItem(
                    rate = rate,
                    isSelected = rate.code == selectedCurrency,
                    onClick = { viewModel.selectCurrency(rate.code) }
                )
            }
        }
    }
}

@Composable
fun MarketRateItem(
    rate: ExchangeRate,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            brush = if (isSelected) {
                Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primary))
            } else {
                Brush.linearGradient(listOf(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)))
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = rate.iconResId ?: R.drawable.placeholder),
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = rate.code,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = rate.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = String.format(Locale.US, "%.2f", rate.median),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Black
                    )
                )
                Text(
                    text = "CUP",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
fun SimpleHeader(
    title: String,
    isDarkTheme: Boolean = true,
    onThemeToggle: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .width(48.dp)
                    .height(4.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }

        IconButton(
            onClick = onThemeToggle,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .size(48.dp)
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
        ) {
            Icon(
                imageVector = if (isDarkTheme) Icons.Rounded.LightMode else Icons.Rounded.DarkMode,
                contentDescription = "Toggle Theme"
            )
        }
    }
}
