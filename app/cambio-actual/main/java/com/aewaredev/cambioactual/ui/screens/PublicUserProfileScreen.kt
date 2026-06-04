package com.aewaredev.cambioactual.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aewaredev.cambioactual.data.model.UiState
import com.aewaredev.cambioactual.ui.components.*
import com.aewaredev.cambioactual.ui.viewmodel.ExchangeViewModel
import com.aewaredev.cambioactual.ui.viewmodel.ProfileViewModel
import com.aewaredev.cambioactual.ui.viewmodel.RatingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicUserProfileScreen(
    userId: Int,
    viewModel: ProfileViewModel,
    exchangeViewModel: ExchangeViewModel,
    ratingViewModel: RatingViewModel,
    onBack: () -> Unit
) {
    val profileState by viewModel.publicProfileState.collectAsState()
    val isDarkTheme by exchangeViewModel.isDarkTheme.collectAsState()

    LaunchedEffect(userId) {
        viewModel.fetchPublicProfile(userId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        SimpleHeader(
            title = "PERFIL",
            isDarkTheme = isDarkTheme,
            onThemeToggle = { exchangeViewModel.toggleTheme() },
            showBack = true,
            onBackClick = onBack
        )

        when (val state = profileState) {
            is UiState.Loading -> LoadingState()
            is UiState.Error -> ErrorState(state.message) { viewModel.fetchPublicProfile(userId) }
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
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
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
                            InfoRow(label = "Rating", value = "${user.ratingAverage}/5")
                        }
                    }
                }
            }
            else -> Unit
        }
    }
}
