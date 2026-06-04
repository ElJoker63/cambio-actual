package com.aewaredev.cambioactual.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VerificationStatus(
    val status: String, // "none", "pending", "manual_review", "approved", "rejected"
    @Json(name = "kyc_details") val kycDetails: String? = null
)
