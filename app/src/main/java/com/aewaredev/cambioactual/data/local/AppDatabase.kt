package com.aewaredev.cambioactual.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aewaredev.cambioactual.data.model.RateHistory

@Database(entities = [RateHistory::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun rateDao(): RateDao

    companion object {
        const val DATABASE_NAME = "cambio_actual_db"
    }
}
