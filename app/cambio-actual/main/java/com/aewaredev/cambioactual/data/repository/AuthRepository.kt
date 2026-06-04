package com.aewaredev.cambioactual.data.repository

import com.aewaredev.cambioactual.data.model.AuthResponse
import com.aewaredev.cambioactual.data.model.LoginRequest
import com.aewaredev.cambioactual.data.model.RegisterRequest
import com.aewaredev.cambioactual.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(request: LoginRequest): Result<AuthResponse>
    suspend fun register(request: RegisterRequest): Result<AuthResponse>
    suspend fun logout()
    val isLoggedIn: Flow<Boolean>
    val currentUser: Flow<UserProfile?>
}
