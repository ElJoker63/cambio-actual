package com.aewaredev.cambioactual.ui.navigation

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.CurrencyBitcoin
import androidx.compose.material.icons.rounded.Sms
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import androidx.room.Room
import com.aewaredev.cambioactual.data.api.NetworkModule
import com.aewaredev.cambioactual.data.local.AppDatabase
import com.aewaredev.cambioactual.data.repository.ExchangeRepositoryImpl
import com.aewaredev.cambioactual.ui.components.UpdateDialog
import com.aewaredev.cambioactual.ui.screens.ConverterScreen
import com.aewaredev.cambioactual.ui.screens.CryptoScreen
import com.aewaredev.cambioactual.ui.screens.MarketScreen
import com.aewaredev.cambioactual.ui.screens.SMSScreen
import com.aewaredev.cambioactual.ui.theme.CambioActualTheme
import com.aewaredev.cambioactual.ui.viewmodel.ExchangeViewModel

@Composable
fun NavApp(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    
    val database = remember {
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration(true).build()
    }

    val viewModel: ExchangeViewModel = viewModel {
        ExchangeViewModel(
            context.applicationContext as Application,
            ExchangeRepositoryImpl(
                NetworkModule.udyatApi, 
                NetworkModule.updateApi, 
                database.rateDao(),
                database.smsDao()
            )
        )
    }

    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val updateInfo by viewModel.updateInfo.collectAsState()
    val isDownloadingUpdate by viewModel.isDownloadingUpdate.collectAsState()

    CambioActualTheme(darkTheme = isDarkTheme) {
        val backStack = remember { mutableStateListOf<Destination>(Destination.Market) }

        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.background,
                    tonalElevation = 0.dp
                ) {
                    val currentKey = backStack.lastOrNull()
                    
                    NavigationBarItem(
                        selected = currentKey is Destination.Market,
                        onClick = { 
                            backStack.clear()
                            backStack.add(Destination.Market)
                        },
                        icon = { Icon(Icons.Rounded.AccountBalance, contentDescription = "Mercado") },
                        label = { Text("Mercado") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        )
                    )
                    NavigationBarItem(
                        selected = currentKey is Destination.Crypto,
                        onClick = { 
                            backStack.clear()
                            backStack.add(Destination.Crypto)
                        },
                        icon = { Icon(Icons.Rounded.CurrencyBitcoin, contentDescription = "Cripto") },
                        label = { Text("Cripto") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        )
                    )
                    NavigationBarItem(
                        selected = currentKey is Destination.SMS,
                        onClick = { 
                            backStack.clear()
                            backStack.add(Destination.SMS)
                        },
                        icon = { Icon(Icons.Rounded.Sms, contentDescription = "SMS") },
                        label = { Text("SMS") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        )
                    )
                    NavigationBarItem(
                        selected = currentKey is Destination.Converter,
                        onClick = { 
                            backStack.clear()
                            backStack.add(Destination.Converter)
                        },
                        icon = { Icon(Icons.Rounded.Calculate, contentDescription = "Convertidor") },
                        label = { Text("Convertidor") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        )
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                NavDisplay(
                    backStack = backStack,
                    modifier = modifier.padding(innerPadding),
                    entryProvider = { key ->
                        when (key) {
                            is Destination.Market -> NavEntry(key) {
                                MarketScreen(viewModel = viewModel)
                            }
                            is Destination.Crypto -> NavEntry(key) {
                                CryptoScreen(viewModel = viewModel)
                            }
                            is Destination.SMS -> NavEntry(key) {
                                SMSScreen(viewModel = viewModel)
                            }
                            is Destination.Converter -> NavEntry(key) {
                                ConverterScreen(viewModel = viewModel)
                            }
                            is Destination.Detail -> NavEntry(key) {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("Detail for ${key.code}", color = MaterialTheme.colorScheme.onBackground)
                                }
                            }
                        }
                    }
                )

                updateInfo?.let { info ->
                    UpdateDialog(
                        updateInfo = info,
                        isDownloading = isDownloadingUpdate,
                        onUpdate = { viewModel.downloadAndInstallUpdate(info) }
                    )
                }
            }
        }
    }
}
