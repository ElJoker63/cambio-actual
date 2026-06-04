package com.aewaredev.cambioactual.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aewaredev.cambioactual.data.model.UiState
import com.aewaredev.cambioactual.data.model.UserProfile
import com.aewaredev.cambioactual.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: UserRepository) : ViewModel() {

    private val _profileState = MutableStateFlow<UiState<UserProfile>>(UiState.Idle)
    val profileState: StateFlow<UiState<UserProfile>> = _profileState

    private val _publicProfileState = MutableStateFlow<UiState<UserProfile>>(UiState.Idle)
    val publicProfileState: StateFlow<UiState<UserProfile>> = _publicProfileState

    fun fetchMyProfile() {
        viewModelScope.launch {
            _profileState.value = UiState.Loading
            val result = repository.getMyProfile()
            result.fold(
                onSuccess = { _profileState.value = UiState.Success(it) },
                onFailure = { 
                    if (it.message?.contains("401") == true) _profileState.value = UiState.Unauthorized
                    else _profileState.value = UiState.Error(it.message ?: "Error fetching profile")
                }
            )
        }
    }

    fun fetchPublicProfile(userId: Int) {
        viewModelScope.launch {
            _publicProfileState.value = UiState.Loading
            val result = repository.getUserById(userId)
            result.fold(
                onSuccess = { _publicProfileState.value = UiState.Success(it) },
                onFailure = { _publicProfileState.value = UiState.Error(it.message ?: "Error fetching public profile") }
            )
        }
    }

    fun fetchPublicProfileByTelegram(telegramId: Long) {
        viewModelScope.launch {
            _publicProfileState.value = UiState.Loading
            val result = repository.getPublicProfileByTelegram(telegramId)
            result.fold(
                onSuccess = { _publicProfileState.value = UiState.Success(it) },
                onFailure = { _publicProfileState.value = UiState.Error(it.message ?: "Error fetching public profile") }
            )
        }
    }

    fun updateProfile(updates: Map<String, Any>) {
        viewModelScope.launch {
            _profileState.value = UiState.Loading
            val result = repository.updateMyProfile(updates)
            result.fold(
                onSuccess = { _profileState.value = UiState.Success(it) },
                onFailure = { _profileState.value = UiState.Error(it.message ?: "Error updating profile") }
            )
        }
    }
}
