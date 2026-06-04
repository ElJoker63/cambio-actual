package com.aewaredev.cambioactual.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aewaredev.cambioactual.data.model.AuthResponse
import com.aewaredev.cambioactual.data.model.LoginRequest
import com.aewaredev.cambioactual.data.model.RegisterRequest
import com.aewaredev.cambioactual.data.model.UiState
import com.aewaredev.cambioactual.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _authState = MutableStateFlow<UiState<AuthResponse>>(UiState.Idle)
    val authState: StateFlow<UiState<AuthResponse>> = _authState

    val isLoggedIn: StateFlow<Boolean> = repository.isLoggedIn
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val currentUser = repository.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = UiState.Loading
            val result = repository.login(LoginRequest(email, password))
            result.fold(
                onSuccess = { _authState.value = UiState.Success(it) },
                onFailure = { _authState.value = UiState.Error(it.message ?: "Login failed") }
            )
        }
    }

    fun register(request: RegisterRequest) {
        viewModelScope.launch {
            _authState.value = UiState.Loading
            val result = repository.register(request)
            result.fold(
                onSuccess = { _authState.value = UiState.Success(it) },
                onFailure = { _authState.value = UiState.Error(it.message ?: "Registration failed") }
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _authState.value = UiState.Idle
        }
    }

    fun resetState() {
        _authState.value = UiState.Idle
    }
}
