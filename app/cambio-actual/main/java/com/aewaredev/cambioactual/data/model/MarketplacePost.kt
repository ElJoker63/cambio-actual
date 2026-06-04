package com.aewaredev.cambioactual.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MarketplacePost(
    val id: Int,
    @Json(name = "user_id") val userId: Int,
    val title: String,
    val description: String,
    val type: String, // "buy" or "sell"
    val category: String,
    val price: Double,
    val currency: String,
    val location: String?,
    val status: String,
    @Json(name = "images_json") val images: List<String> = emptyList(),
    @Json(name = "contact_method") val contactMethod: String?,
    @Json(name = "views_count") val viewsCount: Int = 0,
    val seller: UserProfile? = null,
    @Json(name = "created_at") val createdAt: String,
    @Json(name = "updated_at") val updatedAt: String
)

@JsonClass(generateAdapter = true)
data class MarketplacePostCreate(
    val title: String,
    val description: String,
    val type: String,
    val category: String,
    val price: Double,
    val currency: String = "USD",
    val location: String? = null,
    @Json(name = "images_json") val images: List<String> = emptyList(),
    @Json(name = "contact_method") val contactMethod: String? = null
)
