package com.aewaredev.cambioactual.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Report
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.aewaredev.cambioactual.data.model.UiState
import com.aewaredev.cambioactual.ui.components.*
import com.aewaredev.cambioactual.ui.viewmodel.AuthViewModel
import com.aewaredev.cambioactual.ui.viewmodel.ExchangeViewModel
import com.aewaredev.cambioactual.ui.viewmodel.MarketplaceViewModel
import com.aewaredev.cambioactual.ui.viewmodel.ProfileViewModel
import com.aewaredev.cambioactual.ui.viewmodel.RatingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketplaceDetailScreen(
    postId: Int,
    viewModel: MarketplaceViewModel,
    authViewModel: AuthViewModel,
    exchangeViewModel: ExchangeViewModel,
    profileViewModel: ProfileViewModel,
    ratingViewModel: RatingViewModel,
    onBack: () -> Unit,
    onNavigateToPublicProfile: (Int) -> Unit
) {
    val postDetailState by viewModel.postDetailState.collectAsState()
    val isDarkTheme by exchangeViewModel.isDarkTheme.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(postId) {
        viewModel.fetchPostDetail(postId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        SimpleHeader(
            title = "DETALLE",
            isDarkTheme = isDarkTheme,
            onThemeToggle = { exchangeViewModel.toggleTheme() },
            showBack = true,
            onBackClick = onBack
        )

        when (val state = postDetailState) {
            is UiState.Loading -> LoadingState()
            is UiState.Error -> ErrorState(state.message) { viewModel.fetchPostDetail(postId) }
            is UiState.Success -> {
                val post = state.data
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Images
                    AsyncImage(
                        model = post.images.firstOrNull(),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        contentScale = ContentScale.Crop
                    )

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = post.title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Text(
                            text = "${post.price} ${post.currency}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = post.location ?: "Ubicación no disponible", color = Color.Gray)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "•", color = Color.Gray)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = post.createdAt.split("T").firstOrNull() ?: "", color = Color.Gray)
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(text = "Descripción", fontWeight = FontWeight.Bold)
                        Text(text = post.description)

                        Spacer(modifier = Modifier.height(24.dp))

                        // Security Tip
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "⚠️ Consejo de seguridad",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE65100)
                                )
                                Text(
                                    text = "Cambio Actual no procesa pagos ni garantiza operaciones externas. Revisa la reputación del usuario y evita enviar dinero por adelantado.",
                                    fontSize = 12.sp,
                                    color = Color(0xFFE65100)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // User Info
                        Text(text = "Anunciante", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        post.seller?.let { seller ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { onNavigateToPublicProfile(post.userId) }
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = seller.username ?: "Usuario", fontWeight = FontWeight.Bold)
                                    RatingStars(rating = seller.ratingAverage, count = seller.ratingCount)
                                }
                                if (seller.isVerified) VerificationBadge(status = seller.kycStatus)
                                if (seller.isVip) VipBadge()
                            }

                            Spacer(modifier = Modifier.height(32.dp))

                            Button(
                                onClick = {
                                    val telegramUrl = if (!seller.username.isNullOrBlank()) {
                                        "https://t.me/${seller.username}"
                                    } else if (seller.telegramId != null) {
                                        "tg://user?id=${seller.telegramId}"
                                    } else null

                                    if (telegramUrl != null) {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(telegramUrl))
                                        context.startActivity(intent)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Contactar por Telegram")
                            }
                        } ?: run {
                            Text(text = "Información del anunciante no disponible", color = Color.Gray)
                        }
                    }
                }
            }
            else -> Unit
        }
    }
}
