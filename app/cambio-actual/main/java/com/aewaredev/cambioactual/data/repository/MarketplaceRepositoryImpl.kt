package com.aewaredev.cambioactual.data.repository

import com.aewaredev.cambioactual.data.api.BackendApiService
import com.aewaredev.cambioactual.data.model.MarketplacePost
import com.aewaredev.cambioactual.data.model.MarketplacePostCreate
import com.aewaredev.cambioactual.data.model.SimpleResponse

class MarketplaceRepositoryImpl(private val apiService: BackendApiService) : MarketplaceRepository {

    override suspend fun getPosts(
        page: Int,
        limit: Int,
        type: String?,
        category: String?,
        query: String?,
        currency: String?,
        location: String?,
        minPrice: Double?,
        maxPrice: Double?
    ): Result<List<MarketplacePost>> {
        return try {
            val response = apiService.getPosts(
                page = page,
                limit = limit,
                type = type,
                category = category,
                minPrice = minPrice,
                maxPrice = maxPrice,
                location = location,
                currency = currency,
                query = query
            )
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error fetching posts: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPostById(postId: Int): Result<MarketplacePost> {
        return try {
            val response = apiService.getPostById(postId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error fetching post: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createPost(post: MarketplacePostCreate): Result<MarketplacePost> {
        return try {
            val response = apiService.createPost(post)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error creating post: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deletePost(postId: Int): Result<SimpleResponse> {
        return try {
            val response = apiService.deletePost(postId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error deleting post: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMyPosts(): Result<List<MarketplacePost>> {
        return try {
            val response = apiService.getMyPosts()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error fetching my posts: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
