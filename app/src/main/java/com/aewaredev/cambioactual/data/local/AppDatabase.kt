package com.aewaredev.cambioactual.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aewaredev.cambioactual.data.model.RateHistory
import com.aewaredev.cambioactual.data.model.SmsAlert

@Database(entities = [RateHistory::class, SmsAlert::class], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun rateDao(): RateDao
    abstract fun smsDao(): SmsDao

    companion object {
        const val DATABASE_NAME = "cambio_actual_db"
    }
}
