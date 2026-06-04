package com.aewaredev.cambioactual.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UdyatMsgResponse(
    val date: String,
    val hour: Int,
    val minutes: Int,
    val seconds: Int,
    val statistics: Map<String, UdyatMsgStatistic>
)

@JsonClass(generateAdapter = true)
data class UdyatMsgStatistic(
    val median: Double,
    val messages: List<String>
)
