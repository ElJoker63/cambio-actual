package com.aewaredev.cambioactual.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CloudUpload
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aewaredev.cambioactual.data.model.UiState
import com.aewaredev.cambioactual.ui.components.LoadingState
import com.aewaredev.cambioactual.ui.components.SimpleHeader
import com.aewaredev.cambioactual.ui.viewmodel.ExchangeViewModel
import com.aewaredev.cambioactual.ui.viewmodel.VerificationViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerificationScreen(
    viewModel: VerificationViewModel,
    exchangeViewModel: ExchangeViewModel,
    onBack: () -> Unit
) {
    val statusState by viewModel.statusState.collectAsState()
    val actionState by viewModel.actionState.collectAsState()
    val isDarkTheme by exchangeViewModel.isDarkTheme.collectAsState()
    val context = LocalContext.current

    var selectedDocUri by remember { mutableStateOf<Uri?>(null) }
    var selectedSelfieUri by remember { mutableStateOf<Uri?>(null) }

    val docLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        selectedDocUri = uri
    }
    
    val selfieLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        selectedSelfieUri = uri
    }

    LaunchedEffect(Unit) {
        viewModel.fetchStatus()
    }

    LaunchedEffect(actionState) {
        if (actionState is UiState.Success) {
            viewModel.fetchStatus()
            viewModel.resetActionState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        SimpleHeader(
            title = "VERIFICAR",
            isDarkTheme = isDarkTheme,
            onThemeToggle = { exchangeViewModel.toggleTheme() },
            showBack = true,
            onBackClick = onBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (val state = statusState) {
                is UiState.Loading -> LoadingState()
                is UiState.Error -> {
                    Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                    Button(onClick = { viewModel.fetchStatus() }) { Text("Reintentar") }
                }
                is UiState.Success -> {
                    val status = state.data.status
                    VerificationStatusContent(
                        status = status,
                        kycDetails = state.data.kycDetails,
                        onStart = { viewModel.startVerification() }
                    )

                    if (status == "pending") {
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        Text("Paso 1: Subir Documento de Identidad", fontWeight = FontWeight.Bold)
                        Button(onClick = { docLauncher.launch("image/*") }) {
                            Icon(Icons.Rounded.CloudUpload, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (selectedDocUri != null) "Cambiar Imagen" else "Seleccionar Imagen")
                        }
                        if (selectedDocUri != null) {
                            Button(onClick = {
                                val file = uriToFile(context, selectedDocUri!!)
                                if (file != null) {
                                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                                    val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
                                    viewModel.uploadDocument(body)
                                }
                            }, modifier = Modifier.padding(top = 8.dp)) {
                                Text("Subir Documento")
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text("Paso 2: Subir Selfie", fontWeight = FontWeight.Bold)
                        Button(onClick = { selfieLauncher.launch("image/*") }) {
                            Icon(Icons.Rounded.CloudUpload, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (selectedSelfieUri != null) "Cambiar Imagen" else "Seleccionar Imagen")
                        }
                        if (selectedSelfieUri != null) {
                            Button(onClick = {
                                val file = uriToFile(context, selectedSelfieUri!!)
                                if (file != null) {
                                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                                    val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
                                    viewModel.uploadSelfie(body)
                                }
                            }, modifier = Modifier.padding(top = 8.dp)) {
                                Text("Subir Selfie")
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = { viewModel.submitVerification() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Enviar para Revisión")
                        }
                    }
                }
                else -> Unit
            }

            if (actionState is UiState.Loading) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator()
            }
            if (actionState is UiState.Error) {
                Text(
                    text = (actionState as UiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun VerificationStatusContent(status: String, kycDetails: String?, onStart: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "ESTADO: ${status.uppercase()}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = when (status) {
                    "approved" -> Color(0xFF4CAF50)
                    "rejected" -> Color.Red
                    "manual_review", "pending" -> Color(0xFFFFA000)
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
            
            kycDetails?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = it, textAlign = TextAlign.Center, style = MaterialTheme.typography.bodySmall)
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Rounded.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Sobre la verificación",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
    Text(
        text = "La verificación de identidad permite a los usuarios saber que eres una persona real. Esto aumenta la confianza en tus anuncios y reduce el riesgo de estafas.",
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(top = 4.dp)
    )

    Spacer(modifier = Modifier.height(24.dp))

    if (status == "none" || status == "rejected") {
        Button(onClick = onStart, modifier = Modifier.fillMaxWidth()) {
            Text("Iniciar Nueva Solicitud")
        }
    }
}

fun uriToFile(context: android.content.Context, uri: Uri): File? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()
        file
    } catch (e: Exception) {
        null
    }
}
