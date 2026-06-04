package com.aewaredev.cambioactual.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UdyatHistoricoResponse(
    val coin: String,
    val period: String,
    val data: List<UdyatHistoricoItem>
)

@JsonClass(generateAdapter = true)
data class UdyatHistoricoItem(
    @Json(name = "_id") val id: String,
    val median: Double,
    val cur: String
)
