package com.aewaredev.cambioactual.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aewaredev.cambioactual.data.model.RateHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface RateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRate(rate: RateHistory)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRates(rates: List<RateHistory>)

    @Query("SELECT * FROM rate_history WHERE code = :code ORDER BY timestamp ASC")
    fun getHistoryForCode(code: String): Flow<List<RateHistory>>

    @Query("SELECT * FROM rate_history WHERE code = :code AND timestamp >= :since ORDER BY timestamp ASC")
    fun getHistoryForCodeSince(code: String, since: Long): Flow<List<RateHistory>>

    @Query("SELECT * FROM rate_history WHERE code = :code ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestRateForCode(code: String): RateHistory?
}
