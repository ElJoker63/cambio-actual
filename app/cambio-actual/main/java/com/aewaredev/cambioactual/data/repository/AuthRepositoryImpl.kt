package com.aewaredev.cambioactual.data.repository

import com.aewaredev.cambioactual.data.api.BackendApiService
import com.aewaredev.cambioactual.data.model.AuthResponse
import com.aewaredev.cambioactual.data.model.LoginRequest
import com.aewaredev.cambioactual.data.model.RegisterRequest
import com.aewaredev.cambioactual.data.model.UserProfile
import com.aewaredev.cambioactual.data.preferences.TokenManager
import kotlinx.coroutines.flow.*

class AuthRepositoryImpl(
    private val apiService: BackendApiService,
    private val tokenManager: TokenManager
) : AuthRepository {

    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    override val currentUser: Flow<UserProfile?> = _currentUser.asStateFlow()

    override val isLoggedIn: Flow<Boolean> = tokenManager.accessToken.map { it != null }

    override suspend fun login(request: LoginRequest): Result<AuthResponse> {
        return try {
            val response = apiService.login(request)
            if (response.isSuccessful) {
                val authResponse = response.body()!!

                // Si el backend no devuelve el usuario en el login, lo obtenemos con getMe()
                val userProfile = if (authResponse.user != null) {
                    authResponse.user
                } else {
                    // Guardamos el token temporalmente para que getMe() pueda usarlo
                    tokenManager.saveAuthData(
                        token = authResponse.accessToken,
                        userId = 0,
                        email = "",
                        username = "",
                        isVerified = false,
                        isVip = false
                    )
                    val meResponse = apiService.getMe()
                    if (meResponse.isSuccessful) {
                        meResponse.body()
                    } else {
                        null
                    }
                }

                if (userProfile != null) {
                    tokenManager.saveAuthData(
                        token = authResponse.accessToken,
                        userId = userProfile.id,
                        email = userProfile.email,
                        username = userProfile.username,
                        isVerified = userProfile.isVerified,
                        isVip = userProfile.isVip
                    )
                    _currentUser.value = userProfile
                    Result.success(authResponse.copy(user = userProfile))
                } else {
                    _currentUser.value = null
                    Result.success(authResponse)
                }
            } else {
                Result.failure(Exception("Login failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(request: RegisterRequest): Result<AuthResponse> {
        return try {
            val response = apiService.register(request)
            if (response.isSuccessful) {
                val authResponse = response.body()!!
                
                // Si el backend no devuelve el usuario en el registro, lo obtenemos con getMe()
                val userProfile = if (authResponse.user != null) {
                    authResponse.user
                } else {
                    // Guardamos el token temporalmente para que getMe() pueda usarlo (vía Interceptor)
                    tokenManager.saveAuthData(
                        token = authResponse.accessToken,
                        userId = 0, // Temporal
                        email = "",
                        username = "",
                        isVerified = false,
                        isVip = false
                    )
                    val meResponse = apiService.getMe()
                    if (meResponse.isSuccessful) {
                        meResponse.body()
                    } else {
                        null
                    }
                }

                if (userProfile != null) {
                    tokenManager.saveAuthData(
                        token = authResponse.accessToken,
                        userId = userProfile.id,
                        email = userProfile.email,
                        username = userProfile.username,
                        isVerified = userProfile.isVerified,
                        isVip = userProfile.isVip
                    )
                    _currentUser.value = userProfile
                    Result.success(authResponse.copy(user = userProfile))
                } else {
                    // Si no pudimos obtener el perfil, igual devolvemos éxito con el token
                    // pero el estado del usuario estará incompleto
                    _currentUser.value = null
                    Result.success(authResponse)
                }
            } else {
                Result.failure(Exception("Registration failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        tokenManager.clearAuthData()
        _currentUser.value = null
    }

    suspend fun fetchMe() {
        try {
            val response = apiService.getMe()
            if (response.isSuccessful) {
                _currentUser.value = response.body()
            }
        } catch (e: Exception) {
            // Log or handle error
        }
    }
}
