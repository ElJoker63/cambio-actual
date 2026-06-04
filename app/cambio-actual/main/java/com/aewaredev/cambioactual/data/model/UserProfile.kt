package com.aewaredev.cambioactual.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserProfile(
    val id: Int,
    @Json(name = "telegram_id") val telegramId: Long? = null,
    val username: String? = null,
    @Json(name = "first_name") val firstName: String? = null,
    @Json(name = "last_name") val lastName: String? = null,
    val email: String? = null,
    @Json(name = "is_admin") val isAdmin: Boolean = false,
    @Json(name = "is_active") val isActive: Boolean = true,
    @Json(name = "is_banned") val isBanned: Boolean = false,
    @Json(name = "is_verified") val isVerified: Boolean = false,
    @Json(name = "is_vip") val isVip: Boolean = false,
    @Json(name = "rating_average") val ratingAverage: Double = 0.0,
    @Json(name = "rating_count") val ratingCount: Int = 0,
    @Json(name = "kyc_status") val kycStatus: String = "none",
    @Json(name = "successful_deals") val successfulDeals: Int = 0,
    val infractions: Int = 0,
    @Json(name = "max_infractions") val maxInfractions: Int = 5
)
