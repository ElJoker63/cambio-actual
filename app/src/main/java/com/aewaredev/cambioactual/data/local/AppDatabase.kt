package com.aewaredev.cambioactual.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.aewaredev.cambioactual.data.model.RateHistory
import com.aewaredev.cambioactual.data.model.SmsAlert

@Database(entities = [RateHistory::class, SmsAlert::class], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun rateDao(): RateDao
    abstract fun smsDao(): SmsDao

    companion object {
        const val DATABASE_NAME = "cambio_actual_db"
        
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                ).fallbackToDestructiveMigration(true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
