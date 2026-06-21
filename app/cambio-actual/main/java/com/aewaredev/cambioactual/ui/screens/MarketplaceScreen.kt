package com.aewaredev.cambioactual.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aewaredev.cambioactual.data.model.UiState
import com.aewaredev.cambioactual.ui.components.*
import com.aewaredev.cambioactual.ui.viewmodel.AuthViewModel
import com.aewaredev.cambioactual.ui.viewmodel.ExchangeViewModel
import com.aewaredev.cambioactual.ui.viewmodel.MarketplaceViewModel
import kotlinx.coroutines.flow.map

@Composable
fun MarketplaceScreen(
    viewModel: MarketplaceViewModel,
    authViewModel: AuthViewModel,
    exchangeViewModel: ExchangeViewModel,
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToCreatePost: () -> Unit
) {
    // val postsState by viewModel.postsState.collectAsState()
    // val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    val isDarkTheme by exchangeViewModel.isDarkTheme.collectAsState()
    
    // var searchQuery by remember { mutableStateOf("") }
    // var selectedType by remember { mutableStateOf<String?>(null) }
    
    /*LaunchedEffect(searchQuery, selectedType) {
        viewModel.fetchPosts(type = selectedType, query = searchQuery.ifBlank { null })
    }*/

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            SimpleHeader(
                title = "MARKETPLACE",
                isDarkTheme = isDarkTheme, 
                onThemeToggle = { exchangeViewModel.toggleTheme() },
                showProfile = true,
                onProfileClick = onNavigateToProfile
            )

            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Próximamente",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        /*if (isLoggedIn) {
            FloatingActionButton(
                onClick = onNavigateToCreatePost,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 100.dp, end = 16.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Publicar")
            }
        }*/
    }
}

