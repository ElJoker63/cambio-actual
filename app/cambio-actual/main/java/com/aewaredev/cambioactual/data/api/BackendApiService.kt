package com.aewaredev.cambioactual.data.api

import com.aewaredev.cambioactual.data.model.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface BackendApiService {

    // Auth
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @GET("api/auth/me")
    suspend fun getMe(): Response<UserProfile>

    // Profile (Usando las rutas de auth/me o users si existen)
    @GET("api/users/me")
    suspend fun getMyProfile(): Response<UserProfile>

    @GET("api/users/{user_id}")
    suspend fun getUserById(@Path("user_id") userId: Int): Response<UserProfile>

    @PATCH("api/users/me")
    suspend fun updateMyProfile(@Body updates: Map<String, Any?>): Response<UserProfile>

    @GET("api/users/telegram/{telegram_id}")
    suspend fun getPublicProfileByTelegram(@Path("telegram_id") telegramId: Long): Response<UserProfile>

    // Marketplace
    @GET("api/marketplace/posts")
    suspend fun getPosts(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("category") category: String? = null,
        @Query("type") type: String? = null,
        @Query("min_price") minPrice: Double? = null,
        @Query("max_price") maxPrice: Double? = null,
        @Query("location") location: String? = null,
        @Query("currency") currency: String? = null,
        @Query("query") query: String? = null
    ): Response<List<MarketplacePost>>

    @GET("api/marketplace/posts/{post_id}")
    suspend fun getPostById(@Path("post_id") postId: Int): Response<MarketplacePost>

    @POST("api/marketplace/posts")
    suspend fun createPost(@Body payload: MarketplacePostCreate): Response<MarketplacePost>

    @GET("api/marketplace/my-posts")
    suspend fun getMyPosts(): Response<List<MarketplacePost>>

    @PATCH("api/marketplace/posts/{post_id}")
    suspend fun updatePost(
        @Path("post_id") postId: Int,
        @Body updates: Map<String, Any?>
    ): Response<MarketplacePost>

    @DELETE("api/marketplace/posts/{post_id}")
    suspend fun deletePost(@Path("post_id") postId: Int): Response<SimpleResponse>

    // Ratings
    @POST("api/ratings")
    suspend fun createRating(@Body request: RatingRequest): Response<Rating>

    @GET("api/ratings/user/{user_id}")
    suspend fun getUserRatings(@Path("user_id") userId: Int): Response<List<Rating>>

    @GET("api/ratings/me")
    suspend fun getMyRatings(): Response<List<Rating>>

    // Verification
    @GET("api/verification/status")
    suspend fun getVerificationStatus(): Response<VerificationStatus>

    @POST("api/verification/start")
    suspend fun startVerification(): Response<SimpleResponse>

    @Multipart
    @POST("api/verification/document")
    suspend fun uploadDocument(@Part file: MultipartBody.Part): Response<SimpleResponse>

    @Multipart
    @POST("api/verification/selfie")
    suspend fun uploadSelfie(@Part file: MultipartBody.Part): Response<SimpleResponse>

    @POST("api/verification/submit")
    suspend fun submitVerification(): Response<SimpleResponse>
}
