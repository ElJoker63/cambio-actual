package com.aewaredev.cambioactual.data.repository

import com.aewaredev.cambioactual.data.api.BackendApiService
import com.aewaredev.cambioactual.data.model.Rating
import com.aewaredev.cambioactual.data.model.RatingRequest

class RatingRepositoryImpl(private val apiService: BackendApiService) : RatingRepository {
    override suspend fun createRating(request: RatingRequest): Result<Rating> {
        return try {
            val response = apiService.createRating(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error creating rating: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserRatings(userId: Int): Result<List<Rating>> {
        return try {
            val response = apiService.getUserRatings(userId)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error fetching ratings: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMyRatings(): Result<List<Rating>> {
        return try {
            val response = apiService.getMyRatings()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error fetching my ratings: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
