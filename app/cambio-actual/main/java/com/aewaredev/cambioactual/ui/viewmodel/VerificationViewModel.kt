package com.aewaredev.cambioactual.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aewaredev.cambioactual.data.model.SimpleResponse
import com.aewaredev.cambioactual.data.model.UiState
import com.aewaredev.cambioactual.data.model.VerificationStatus
import com.aewaredev.cambioactual.data.repository.VerificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class VerificationViewModel(private val repository: VerificationRepository) : ViewModel() {

    private val _statusState = MutableStateFlow<UiState<VerificationStatus>>(UiState.Idle)
    val statusState: StateFlow<UiState<VerificationStatus>> = _statusState

    private val _actionState = MutableStateFlow<UiState<SimpleResponse>>(UiState.Idle)
    val actionState: StateFlow<UiState<SimpleResponse>> = _actionState

    fun fetchStatus() {
        viewModelScope.launch {
            _statusState.value = UiState.Loading
            val result = repository.getVerificationStatus()
            result.fold(
                onSuccess = { _statusState.value = UiState.Success(it) },
                onFailure = { _statusState.value = UiState.Error(it.message ?: "Error fetching status") }
            )
        }
    }

    fun startVerification() {
        viewModelScope.launch {
            _actionState.value = UiState.Loading
            val result = repository.startVerification()
            result.fold(
                onSuccess = { _actionState.value = UiState.Success(it) },
                onFailure = { _actionState.value = UiState.Error(it.message ?: "Error starting verification") }
            )
        }
    }

    fun uploadDocument(part: MultipartBody.Part) {
        viewModelScope.launch {
            _actionState.value = UiState.Loading
            val result = repository.uploadDocument(part)
            result.fold(
                onSuccess = { _actionState.value = UiState.Success(it) },
                onFailure = { _actionState.value = UiState.Error(it.message ?: "Error uploading document") }
            )
        }
    }

    fun uploadSelfie(part: MultipartBody.Part) {
        viewModelScope.launch {
            _actionState.value = UiState.Loading
            val result = repository.uploadSelfie(part)
            result.fold(
                onSuccess = { _actionState.value = UiState.Success(it) },
                onFailure = { _actionState.value = UiState.Error(it.message ?: "Error uploading selfie") }
            )
        }
    }

    fun submitVerification() {
        viewModelScope.launch {
            _actionState.value = UiState.Loading
            val result = repository.submitVerification()
            result.fold(
                onSuccess = { _actionState.value = UiState.Success(it) },
                onFailure = { _actionState.value = UiState.Error(it.message ?: "Error submitting verification") }
            )
        }
    }

    fun resetActionState() {
        _actionState.value = UiState.Idle
    }
}
