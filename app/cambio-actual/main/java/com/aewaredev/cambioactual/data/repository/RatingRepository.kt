package com.aewaredev.cambioactual.data.repository

import com.aewaredev.cambioactual.data.model.Rating
import com.aewaredev.cambioactual.data.model.RatingRequest

interface RatingRepository {
    suspend fun createRating(request: RatingRequest): Result<Rating>
    suspend fun getUserRatings(userId: Int): Result<List<Rating>>
    suspend fun getMyRatings(): Result<List<Rating>>
}
