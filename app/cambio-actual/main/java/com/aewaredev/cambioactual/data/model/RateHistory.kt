package com.aewaredev.cambioactual.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "rate_history",
    indices = [Index(value = ["code", "timestamp"], unique = true)]
)
data class RateHistory(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String,
    val value: Double,
    val timestamp: Long
)
