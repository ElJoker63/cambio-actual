package com.aewaredev.cambioactual.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.aewaredev.cambioactual.ui.components.RateItem
import com.aewaredev.cambioactual.ui.components.SimpleHeader
import com.aewaredev.cambioactual.ui.components.TrendGraph
import com.aewaredev.cambioactual.ui.viewmodel.ExchangeViewModel
import java.util.Locale

@Composable
fun MarketScreen(
    viewModel: ExchangeViewModel,
    onNavigateToProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rates by viewModel.informalRates.collectAsState()
    val historyData by viewModel.historyData.collectAsState()
    val selectedCurrency by viewModel.selectedCurrency.collectAsState()
    val selectedPeriod by viewModel.selectedPeriod.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    
    val currentRate = rates.find { it.code == selectedCurrency }

    LaunchedEffect(rates) {
        if (rates.isNotEmpty() && rates.none { it.code == selectedCurrency }) {
            viewModel.selectCurrency(rates.first().code)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        SimpleHeader(
            title = "CAMBIO ACTUAL",
            isDarkTheme = isDarkTheme,
            onThemeToggle = { viewModel.toggleTheme() },
            showProfile = true,
            onProfileClick = onNavigateToProfile
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
                RateItem(
                    rate = rate,
                    isSelected = rate.code == selectedCurrency,
                    onClick = { viewModel.selectCurrency(rate.code) }
                )
            }
        }
    }
}

// MarketRateItem removed and replaced by RateItem in components package

