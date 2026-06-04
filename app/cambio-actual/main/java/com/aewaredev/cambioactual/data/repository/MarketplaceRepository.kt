package com.aewaredev.cambioactual.data.repository

import com.aewaredev.cambioactual.data.model.MarketplacePost
import com.aewaredev.cambioactual.data.model.MarketplacePostCreate
import com.aewaredev.cambioactual.data.model.SimpleResponse

interface MarketplaceRepository {
    suspend fun getPosts(
        page: Int = 1,
        limit: Int = 20,
        type: String? = null,
        category: String? = null,
        query: String? = null,
        currency: String? = null,
        location: String? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null
    ): Result<List<MarketplacePost>>

    suspend fun getPostById(postId: Int): Result<MarketplacePost>

    suspend fun createPost(
        post: MarketplacePostCreate
    ): Result<MarketplacePost>

    suspend fun deletePost(postId: Int): Result<SimpleResponse>
    suspend fun getMyPosts(): Result<List<MarketplacePost>>
}
