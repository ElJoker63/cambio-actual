package com.aewaredev.cambioactual.data.repository

import com.aewaredev.cambioactual.data.model.ExchangeRate
import com.aewaredev.cambioactual.data.model.RateHistory
import com.aewaredev.cambioactual.data.model.SmsAlert
import com.aewaredev.cambioactual.data.model.UpdateInfo
import kotlinx.coroutines.flow.Flow

interface ExchangeRepository {
    fun getInformalRates(): Flow<List<ExchangeRate>>
    fun getCryptoRates(): Flow<List<ExchangeRate>>
    fun getRateHistory(code: String): Flow<List<RateHistory>>
    fun getRateHistorySince(code: String, since: Long): Flow<List<RateHistory>>
    fun getSmsAlerts(): Flow<List<SmsAlert>>
    suspend fun refreshRates()
    suspend fun refreshMessages()
    suspend fun refreshHistory(code: String, period: String)
    suspend fun checkForUpdate(): UpdateInfo?
}
