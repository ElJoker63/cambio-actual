package com.aewaredev.cambioactual.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Rating(
    val id: Int,
    @Json(name = "from_user_id") val fromUserId: Int,
    @Json(name = "to_user_id") val toUserId: Int,
    val stars: Int,
    val comment: String?,
    @Json(name = "created_at") val createdAt: String
)

@JsonClass(generateAdapter = true)
data class RatingRequest(
    @Json(name = "to_user_id") val toUserId: Int,
    val stars: Int,
    val comment: String?
)
