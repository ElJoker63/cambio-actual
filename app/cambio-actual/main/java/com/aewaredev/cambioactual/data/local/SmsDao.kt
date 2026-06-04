package com.aewaredev.cambioactual.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aewaredev.cambioactual.data.model.SmsAlert
import kotlinx.coroutines.flow.Flow

@Dao
interface SmsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSms(alert: SmsAlert)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSmsList(alerts: List<SmsAlert>)

    @Query("SELECT * FROM sms_alerts ORDER BY timestamp DESC")
    fun getSmsAlerts(): Flow<List<SmsAlert>>

    @Query("DELETE FROM sms_alerts")
    suspend fun clearSms()
}
