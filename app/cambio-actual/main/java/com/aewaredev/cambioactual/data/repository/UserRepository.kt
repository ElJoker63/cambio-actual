package com.aewaredev.cambioactual.data.repository

import com.aewaredev.cambioactual.data.model.UserProfile

interface UserRepository {
    suspend fun getMyProfile(): Result<UserProfile>
    suspend fun updateMyProfile(updates: Map<String, Any>): Result<UserProfile>
    suspend fun getPublicProfileByTelegram(telegramId: Long): Result<UserProfile>
    suspend fun getUserById(userId: Int): Result<UserProfile>
}
