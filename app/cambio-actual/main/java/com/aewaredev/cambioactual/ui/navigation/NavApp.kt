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
import com.aewaredev.cambioactual.ui.components.LoadingDialog
import com.aewaredev.cambioactual.ui.components.UpdateDialog
import com.aewaredev.cambioactual.ui.screens.ConverterScreen
import com.aewaredev.cambioactual.ui.screens.CryptoScreen
import com.aewaredev.cambioactual.ui.screens.MarketScreen
import com.aewaredev.cambioactual.ui.screens.SMSScreen
import com.aewaredev.cambioactual.ui.theme.CambioActualTheme
import com.aewaredev.cambioactual.ui.viewmodel.ExchangeViewModel

import com.aewaredev.cambioactual.data.repository.*
import com.aewaredev.cambioactual.data.preferences.TokenManager
import com.aewaredev.cambioactual.ui.viewmodel.*
import com.aewaredev.cambioactual.ui.screens.*
import androidx.compose.material.icons.rounded.Storefront

@Composable
fun NavApp(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    
    val database = remember { AppDatabase.getInstance(context) }
    val tokenManager = remember { TokenManager(context) }
    
    val backendApi = remember { NetworkModule.provideBackendApi(context) }

    val exchangeViewModel: ExchangeViewModel = viewModel {
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

    val authViewModel: AuthViewModel = viewModel {
        AuthViewModel(AuthRepositoryImpl(backendApi, tokenManager))
    }

    val marketplaceViewModel: MarketplaceViewModel = viewModel {
        MarketplaceViewModel(MarketplaceRepositoryImpl(backendApi))
    }

    val profileViewModel: ProfileViewModel = viewModel {
        ProfileViewModel(UserRepositoryImpl(backendApi))
    }

    val ratingViewModel: RatingViewModel = viewModel {
        RatingViewModel(RatingRepositoryImpl(backendApi))
    }

    val verificationViewModel: VerificationViewModel = viewModel {
        VerificationViewModel(VerificationRepositoryImpl(backendApi))
    }

    val isDarkTheme by exchangeViewModel.isDarkTheme.collectAsState()
    val updateInfo by exchangeViewModel.updateInfo.collectAsState()
    val isDownloadingUpdate by exchangeViewModel.isDownloadingUpdate.collectAsState()
    val initialSyncInProgress by exchangeViewModel.initialSyncInProgress.collectAsState()

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
                        icon = { Icon(Icons.Rounded.AccountBalance, contentDescription = "Cambio") },
                        label = { Text("Cambio") },
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
                        selected = currentKey is Destination.Marketplace,
                        onClick = { 
                            backStack.clear()
                            backStack.add(Destination.Marketplace)
                        },
                        icon = { Icon(Icons.Rounded.Storefront, contentDescription = "Marketplace") },
                        label = { Text("Market") },
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
                    /*NavigationBarItem(
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
                    )*/
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
                                MarketScreen(
                                    viewModel = exchangeViewModel,
                                    onNavigateToProfile = { backStack.add(Destination.Profile) }
                                )
                            }
                            is Destination.Crypto -> NavEntry(key) {
                                CryptoScreen(
                                    viewModel = exchangeViewModel,
                                    onNavigateToProfile = { backStack.add(Destination.Profile) }
                                )
                            }
                            is Destination.Marketplace -> NavEntry(key) {
                                MarketplaceScreen(
                                    viewModel = marketplaceViewModel,
                                    authViewModel = authViewModel,
                                    exchangeViewModel = exchangeViewModel,
                                    onNavigateToDetail = { postId -> backStack.add(Destination.MarketplaceDetail(postId)) },
                                    onNavigateToProfile = { backStack.add(Destination.Profile) },
                                    onNavigateToCreatePost = { backStack.add(Destination.CreatePost) }
                                )
                            }
                            is Destination.MarketplaceDetail -> NavEntry(key) {
                                MarketplaceDetailScreen(
                                    postId = key.postId,
                                    viewModel = marketplaceViewModel,
                                    authViewModel = authViewModel,
                                    exchangeViewModel = exchangeViewModel,
                                    profileViewModel = profileViewModel,
                                    ratingViewModel = ratingViewModel,
                                    onBack = { backStack.removeAt(backStack.lastIndex) },
                                    onNavigateToPublicProfile = { backStack.add(Destination.PublicProfile(it)) }
                                )
                            }
                            is Destination.CreatePost -> NavEntry(key) {
                                CreatePostScreen(
                                    viewModel = marketplaceViewModel,
                                    authViewModel = authViewModel,
                                    exchangeViewModel = exchangeViewModel,
                                    onBack = { backStack.removeAt(backStack.lastIndex) },
                                    onNavigateToVerification = { backStack.add(Destination.Verification) }
                                )
                            }
                            is Destination.Profile -> NavEntry(key) {
                                ProfileScreen(
                                    viewModel = profileViewModel,
                                    authViewModel = authViewModel,
                                    exchangeViewModel = exchangeViewModel,
                                    onBack = { backStack.removeAt(backStack.lastIndex) },
                                    onNavigateToLogin = { backStack.add(Destination.Login) },
                                    onNavigateToMyPosts = { backStack.add(Destination.MyPosts) },
                                    onNavigateToVerification = { backStack.add(Destination.Verification) }
                                )
                            }
                            is Destination.PublicProfile -> NavEntry(key) {
                                PublicUserProfileScreen(
                                    userId = key.userId,
                                    viewModel = profileViewModel,
                                    exchangeViewModel = exchangeViewModel,
                                    ratingViewModel = ratingViewModel,
                                    onBack = { backStack.removeAt(backStack.lastIndex) }
                                )
                            }
                            is Destination.Login -> NavEntry(key) {
                                LoginScreen(
                                    viewModel = authViewModel,
                                    exchangeViewModel = exchangeViewModel,
                                    onBack = { backStack.removeAt(backStack.lastIndex) },
                                    onNavigateToRegister = { backStack.add(Destination.Register) },
                                    onLoginSuccess = {
                                        backStack.clear()
                                        backStack.add(Destination.Marketplace)
                                    }
                                )
                            }
                            is Destination.Register -> NavEntry(key) {
                                RegisterScreen(
                                    viewModel = authViewModel,
                                    exchangeViewModel = exchangeViewModel,
                                    onBack = { backStack.removeAt(backStack.lastIndex) },
                                    onNavigateToLogin = { backStack.add(Destination.Login) }
                                )
                            }
                            is Destination.Verification -> NavEntry(key) {
                                VerificationScreen(
                                    viewModel = verificationViewModel,
                                    exchangeViewModel = exchangeViewModel,
                                    onBack = { backStack.removeAt(backStack.lastIndex) }
                                )
                            }
                            is Destination.SMS -> NavEntry(key) {
                                SMSScreen(viewModel = exchangeViewModel)
                            }
                            is Destination.Converter -> NavEntry(key) {
                                ConverterScreen(
                                    viewModel = exchangeViewModel,
                                    onNavigateToProfile = { backStack.add(Destination.Profile) }
                                )
                            }
                            is Destination.Detail -> NavEntry(key) {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("Detail for ${key.code}", color = MaterialTheme.colorScheme.onBackground)
                                }
                            }
                            else -> NavEntry(key) { Box(Modifier.fillMaxSize()) }
                        }
                    }
                )

                updateInfo?.let { info ->
                    UpdateDialog(
                        updateInfo = info,
                        isDownloading = isDownloadingUpdate,
                        onUpdate = { exchangeViewModel.downloadAndInstallUpdate(info) }
                    )
                }

                if (initialSyncInProgress) {
                    LoadingDialog()
                }
            }
        }
    }
}
