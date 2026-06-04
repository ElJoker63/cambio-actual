package com.aewaredev.cambioactual.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.aewaredev.cambioactual.data.model.RegisterRequest
import com.aewaredev.cambioactual.data.model.UiState
import com.aewaredev.cambioactual.ui.components.SimpleHeader
import com.aewaredev.cambioactual.ui.viewmodel.AuthViewModel
import com.aewaredev.cambioactual.ui.viewmodel.ExchangeViewModel

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    exchangeViewModel: ExchangeViewModel,
    onBack: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val authState by viewModel.authState.collectAsState()
    val isDarkTheme by exchangeViewModel.isDarkTheme.collectAsState()
    
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }

    LaunchedEffect(authState) {
        if (authState is UiState.Success) {
            viewModel.resetState()
            onBack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SimpleHeader(
            title = "REGISTRO",
            isDarkTheme = isDarkTheme,
            onThemeToggle = { exchangeViewModel.toggleTheme() },
            showBack = true,
            onBackClick = {
                viewModel.resetState()
                onBack()
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "NUEVA CUENTA",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Usuario") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                if (authState is UiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(32.dp))
                } else {
                    Button(
                        onClick = { 
                            viewModel.register(
                                RegisterRequest(
                                    email = email,
                                    password = password,
                                    username = username,
                                    firstName = firstName,
                                    lastName = lastName
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        enabled = email.isNotBlank() && password.isNotBlank(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("REGISTRARSE", fontWeight = FontWeight.Bold)
                    }
                    
                    TextButton(
                        onClick = {
                            viewModel.resetState()
                            onNavigateToLogin()
                        },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("¿Ya tienes cuenta? Inicia sesión")
                    }
                }
                
                if (authState is UiState.Error) {
                    Text(
                        text = (authState as UiState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 16.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1.2f))
    }
}
