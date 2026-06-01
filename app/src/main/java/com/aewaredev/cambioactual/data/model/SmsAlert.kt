package com.aewaredev.cambioactual.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sms_alerts",
    indices = [Index(value = ["title", "message", "time"], unique = true)]
)
data class SmsAlert(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val message: String,
    val time: String,
    val timestamp: Long = System.currentTimeMillis()
)
