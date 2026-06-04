package com.aewaredev.cambioactual.data.repository

import com.aewaredev.cambioactual.data.api.BackendApiService
import com.aewaredev.cambioactual.data.model.UserProfile

class UserRepositoryImpl(private val apiService: BackendApiService) : UserRepository {
    override suspend fun getMyProfile(): Result<UserProfile> {
        return try {
            val response = apiService.getMyProfile()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error fetching profile: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateMyProfile(updates: Map<String, Any>): Result<UserProfile> {
        return try {
            val response = apiService.updateMyProfile(updates)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error updating profile: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPublicProfileByTelegram(telegramId: Long): Result<UserProfile> {
        return try {
            val response = apiService.getPublicProfileByTelegram(telegramId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error fetching public profile: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserById(userId: Int): Result<UserProfile> {
        return try {
            val response = apiService.getUserById(userId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error fetching user: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
