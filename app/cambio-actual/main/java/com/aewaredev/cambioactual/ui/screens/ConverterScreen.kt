package com.aewaredev.cambioactual.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aewaredev.cambioactual.R
import com.aewaredev.cambioactual.data.model.ExchangeRate
import com.aewaredev.cambioactual.ui.components.SimpleHeader
import com.aewaredev.cambioactual.ui.theme.GradientBlue
import com.aewaredev.cambioactual.ui.viewmodel.ExchangeViewModel
import java.util.Locale

@Composable
fun ConverterScreen(
    viewModel: ExchangeViewModel,
    onNavigateToProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    var amount by remember { mutableStateOf("1") }
    val informalRates by viewModel.informalRates.collectAsState()
    val cryptoRates by viewModel.cryptoRates.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()

    val cupRate = remember {
        ExchangeRate(
            code = "CUP",
            name = "Peso Cubano",
            buy = 1.0,
            sell = 1.0,
            median = 1.0,
            lastUpdated = "",
            iconResId = R.drawable.ic_mlc // Using MLC icon for CUP as placeholder or similar
        )
    }

    val allRates = remember(informalRates, cryptoRates) {
        val list = mutableListOf<ExchangeRate>()
        list.addAll(informalRates)
        list.addAll(cryptoRates)
        if (list.none { it.code == "CUP" }) {
            list.add(cupRate)
        }
        list
    }

    var selectedFromCode by remember { mutableStateOf("USD") }
    var showFromSelector by remember { mutableStateOf(false) }

    val fromRate = allRates.find { it.code == selectedFromCode } ?: cupRate

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        SimpleHeader(
            title = "CONVERTIDOR",
            isDarkTheme = isDarkTheme,
            onThemeToggle = { viewModel.toggleTheme() },
            showProfile = true,
            onProfileClick = onNavigateToProfile
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 24.dp),
            shape = RoundedCornerShape(32.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(
                width = 2.dp,
                brush = Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primary))
            )
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CANTIDAD BASE",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        letterSpacing = 1.sp
                    )
                    
                    Box {
                        Surface(
                            onClick = { showFromSelector = true },
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(id = fromRate.iconResId ?: R.drawable.placeholder),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = Color.Unspecified
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = selectedFromCode,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.width(4.dp))
                                Icon(
                                    Icons.Rounded.SwapVert,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showFromSelector,
                            onDismissRequest = { showFromSelector = false },
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surface)
                                .heightIn(max = 400.dp)
                        ) {
                            allRates.forEach { rate ->
                                DropdownMenuItem(
                                    text = { 
                                        Column {
                                            Text(rate.code, fontWeight = FontWeight.Bold)
                                            Text(rate.name, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                        }
                                    },
                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(id = rate.iconResId ?: R.drawable.placeholder),
                                            contentDescription = null,
                                            modifier = Modifier.size(28.dp),
                                            tint = Color.Unspecified
                                        )
                                    },
                                    onClick = {
                                        selectedFromCode = rate.code
                                        showFromSelector = false
                                    }
                                )
                            }
                        }
                    }
                }

                TextField(
                    value = amount,
                    onValueChange = { amount = it },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = MaterialTheme.typography.headlineMedium.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Black
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = MaterialTheme.colorScheme.primary
                    ),
                    singleLine = true
                )
            }
        }

        Text(
            text = "RESULTADOS DE CONVERSIÓN",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 16.dp, start = 4.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(bottom = 100.dp)
        ) {
            val numericAmount = amount.toDoubleOrNull() ?: 0.0
            val amountInCup = numericAmount * fromRate.median
            
            allRates.filter { it.code != selectedFromCode }
                .sortedByDescending { it.code == "CUP" }
                .forEach { rate ->
                val convertedValue = if (rate.median != 0.0) amountInCup / rate.median else 0.0
                ConversionResultRow(
                    code = rate.code,
                    value = convertedValue,
                    name = rate.name
                )
            }
        }
    }
}


@Composable
fun ConversionResultRow(code: String, value: Double, name: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            brush = Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.8f), MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)))
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = code,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = name,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
            Text(
                text = if (value < 0.01 && value > 0) String.format(Locale.US, "%.6f", value) 
                       else String.format(Locale.US, "%.2f", value),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

