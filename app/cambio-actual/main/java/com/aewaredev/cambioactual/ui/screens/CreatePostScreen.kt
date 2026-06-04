package com.aewaredev.cambioactual.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aewaredev.cambioactual.data.model.MarketplacePostCreate
import com.aewaredev.cambioactual.data.model.UiState
import com.aewaredev.cambioactual.ui.components.LoadingState
import com.aewaredev.cambioactual.ui.components.SimpleHeader
import com.aewaredev.cambioactual.ui.viewmodel.AuthViewModel
import com.aewaredev.cambioactual.ui.viewmodel.ExchangeViewModel
import com.aewaredev.cambioactual.ui.viewmodel.MarketplaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    viewModel: MarketplaceViewModel,
    authViewModel: AuthViewModel,
    exchangeViewModel: ExchangeViewModel,
    onBack: () -> Unit,
    onNavigateToVerification: () -> Unit
) {
    val createPostState by viewModel.createPostState.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val isDarkTheme by exchangeViewModel.isDarkTheme.collectAsState()
    
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("sell") }
    var category by remember { mutableStateOf("Divisas") }
    var price by remember { mutableStateOf("") }
    var currency by remember { mutableStateOf("CUP") }
    var location by remember { mutableStateOf("La Habana") }
    var contactMethod by remember { mutableStateOf("telegram") }

    LaunchedEffect(createPostState) {
        if (createPostState is UiState.Success) {
            viewModel.resetCreatePostState()
            onBack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        SimpleHeader(
            title = "PUBLICAR",
            isDarkTheme = isDarkTheme,
            onThemeToggle = { exchangeViewModel.toggleTheme() },
            showBack = true,
            onBackClick = onBack
        )

        if (currentUser == null) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
            ) {
                Text("Debes iniciar sesión para publicar anuncios.")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onBack) {
                    Text("Volver")
                }
            }
        } else if (currentUser?.isVerified != true) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text("Debes verificar tu identidad para publicar anuncios.")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onNavigateToVerification) {
                    Text("Verificarme")
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                Spacer(modifier = Modifier.height(8.dp))
                
                Text("Tipo de anuncio")
                Row {
                    RadioButton(selected = type == "sell", onClick = { type = "sell" })
                    Text("Venta", modifier = Modifier.padding(start = 8.dp, top = 12.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    RadioButton(selected = type == "buy", onClick = { type = "buy" })
                    Text("Compra", modifier = Modifier.padding(start = 8.dp, top = 12.dp))
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Precio") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = currency, onValueChange = { currency = it }, label = { Text("Moneda") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Ubicación") }, modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(24.dp))
                
                if (createPostState is UiState.Loading) {
                    LoadingState()
                } else {
                    Button(
                        onClick = {
                            viewModel.createPost(
                                MarketplacePostCreate(
                                    title = title,
                                    description = description,
                                    type = type,
                                    category = category,
                                    price = price.toDoubleOrNull() ?: 0.0,
                                    currency = currency,
                                    location = location,
                                    contactMethod = contactMethod,
                                    images = emptyList()
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Publicar")
                    }
                }
            }
        }
    }
}
