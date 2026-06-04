package com.aewaredev.cambioactual.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aewaredev.cambioactual.data.model.UiState
import com.aewaredev.cambioactual.ui.components.*
import com.aewaredev.cambioactual.ui.viewmodel.AuthViewModel
import com.aewaredev.cambioactual.ui.viewmodel.ExchangeViewModel
import com.aewaredev.cambioactual.ui.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    authViewModel: AuthViewModel,
    exchangeViewModel: ExchangeViewModel,
    onBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToMyPosts: () -> Unit,
    onNavigateToVerification: () -> Unit
) {
    val profileState by viewModel.profileState.collectAsState()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    val isDarkTheme by exchangeViewModel.isDarkTheme.collectAsState()

    LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn) {
            onNavigateToLogin()
        } else {
            viewModel.fetchMyProfile()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        SimpleHeader(
            title = "MI PERFIL",
            isDarkTheme = isDarkTheme,
            onThemeToggle = { exchangeViewModel.toggleTheme() },
            showBack = true,
            onBackClick = onBack
        )

        when (val state = profileState) {
            is UiState.Loading -> LoadingState()
            is UiState.Error -> ErrorState(state.message) { viewModel.fetchMyProfile() }
            is UiState.Unauthorized -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Button(onClick = onNavigateToLogin) { Text("Iniciar sesión") }
                }
            }
            is UiState.Success -> {
                val user = state.data
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = user.username ?: "Usuario",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = user.email ?: "", color = Color.Gray)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        VerificationBadge(status = user.kycStatus, showText = true)
                        if (user.isVip) {
                            Spacer(modifier = Modifier.width(8.dp))
                            VipBadge()
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            InfoRow(label = "Negocios exitosos", value = user.successfulDeals.toString())
                            InfoRow(label = "Infracciones", value = "${user.infractions}/${user.maxInfractions}")
                            InfoRow(label = "Rating", value = "${user.ratingAverage}/5 (${user.ratingCount})")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Button(
                        onClick = onNavigateToMyPosts,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Mis anuncios")
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedButton(
                        onClick = onNavigateToVerification,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Verificar identidad")
                    }
                }
            }
            else -> Unit
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.Gray)
        Text(text = value, fontWeight = FontWeight.Bold)
    }
}
