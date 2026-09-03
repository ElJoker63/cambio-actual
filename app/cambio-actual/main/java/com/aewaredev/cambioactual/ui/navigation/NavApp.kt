package com.aewaredev.cambioactual.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.CurrencyBitcoin
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.aewaredev.cambioactual.ui.components.LoadingDialog
import com.aewaredev.cambioactual.ui.components.UpdateDialog
import com.aewaredev.cambioactual.ui.screens.*
import com.aewaredev.cambioactual.ui.theme.CambioActualTheme
import com.aewaredev.cambioactual.ui.viewmodel.*

@Composable
fun NavApp(modifier: Modifier = Modifier) {
    val exchangeViewModel: ExchangeViewModel = hiltViewModel()
    val authViewModel: AuthViewModel = hiltViewModel()
    val marketplaceViewModel: MarketplaceViewModel = hiltViewModel()
    val profileViewModel: ProfileViewModel = hiltViewModel()
    val ratingViewModel: RatingViewModel = hiltViewModel()
    val verificationViewModel: VerificationViewModel = hiltViewModel()

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
                    
                    val items = listOf(
                        NavigationItem(Destination.Market, Icons.Rounded.AccountBalance, "Cambio"),
                        NavigationItem(Destination.Crypto, Icons.Rounded.CurrencyBitcoin, "Cripto"),
                        NavigationItem(Destination.Marketplace, Icons.Rounded.Storefront, "Market"),
                        NavigationItem(Destination.Converter, Icons.Rounded.Calculate, "Convertidor")
                    )

                    items.forEach { item ->
                        NavigationBarItem(
                            selected = currentKey?.let { isSameDestination(it, item.destination) } ?: false,
                            onClick = { 
                                backStack.clear()
                                backStack.add(item.destination)
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            )
                        )
                    }
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

private data class NavigationItem(
    val destination: Destination,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: String
)

private fun isSameDestination(current: Destination, target: Destination): Boolean {
    return when (target) {
        Destination.Market -> current is Destination.Market
        Destination.Crypto -> current is Destination.Crypto
        Destination.Marketplace -> current is Destination.Marketplace
        Destination.Converter -> current is Destination.Converter
        Destination.SMS -> current is Destination.SMS
        Destination.Profile -> current is Destination.Profile
        else -> false
    }
}
