package com.aewaredev.cambioactual.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UdyatCoinRate(
    val coin: String,
    val last: UdyatValue
)

@JsonClass(generateAdapter = true)
data class UdyatValue(
    val value: Double
)
