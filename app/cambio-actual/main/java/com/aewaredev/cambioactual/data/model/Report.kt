package com.aewaredev.cambioactual.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserReportRequest(
    @Json(name = "user_id") val userId: Int,
    val reason: String,
    val description: String?
)

@JsonClass(generateAdapter = true)
data class PostReportRequest(
    @Json(name = "post_id") val postId: Int,
    val reason: String,
    val description: String?
)
