package com.aewaredev.cambioactual.data.model

data class ExchangeRate(
    val code: String,
    val name: String,
    val buy: Double,
    val sell: Double,
    val median: Double,
    val lastUpdated: String,
    val iconResId: Int? = null,
    val trend: Trend = Trend.STABLE
)

enum class Trend {
    UP, DOWN, STABLE
}
