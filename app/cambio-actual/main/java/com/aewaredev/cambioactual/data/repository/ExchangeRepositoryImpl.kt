package com.aewaredev.cambioactual.data.repository

import com.aewaredev.cambioactual.R
import com.aewaredev.cambioactual.data.api.UdyatApiService
import com.aewaredev.cambioactual.data.api.UpdateApiService
import com.aewaredev.cambioactual.data.local.RateDao
import com.aewaredev.cambioactual.data.local.SmsDao
import com.aewaredev.cambioactual.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Locale

class ExchangeRepositoryImpl(
    private val apiService: UdyatApiService,
    private val updateApi: UpdateApiService,
    private val rateDao: RateDao,
    private val smsDao: SmsDao
) : ExchangeRepository {

    private val _informalRates = MutableStateFlow<List<ExchangeRate>>(emptyList())
    private val _cryptoRates = MutableStateFlow<List<ExchangeRate>>(emptyList())

    override fun getInformalRates(): Flow<List<ExchangeRate>> = _informalRates.asStateFlow()
    override fun getCryptoRates(): Flow<List<ExchangeRate>> = _cryptoRates.asStateFlow()
    override fun getSmsAlerts(): Flow<List<SmsAlert>> = smsDao.getSmsAlerts()

    override fun getRateHistory(code: String): Flow<List<RateHistory>> = rateDao.getHistoryForCode(code)
    override fun getRateHistorySince(code: String, since: Long): Flow<List<RateHistory>> = 
        rateDao.getHistoryForCodeSince(code, since)

    private val allRelevantCodes = listOf("USD", "ECU", "MLC", "ZELLE", "MXN", "CAD", "BTC", "BNB", "TRX", "USDT", "CLA")
    private val marketCodes = listOf("USD", "ECU", "MLC", "ZELLE", "MXN", "CAD", "CLA")
    private val cryptoCodes = listOf("BTC", "BNB", "TRX", "USDT")

    suspend fun loadLocalData() {
        val informal = mutableListOf<ExchangeRate>()
        marketCodes.forEach { code ->
            val latest = rateDao.getLatestRateForCode(code)
            if (latest != null) {
                informal.add(ExchangeRate(
                    code = code,
                    name = getCurrencyName(code),
                    buy = latest.value,
                    sell = latest.value,
                    median = latest.value,
                    lastUpdated = "Local",
                    trend = Trend.STABLE,
                    iconResId = getCurrencyIcon(code)
                ))
            }
        }
        _informalRates.value = informal

        val crypto = mutableListOf<ExchangeRate>()
        cryptoCodes.forEach { code ->
            val latest = rateDao.getLatestRateForCode(code)
            if (latest != null) {
                crypto.add(ExchangeRate(
                    code = code,
                    name = getCurrencyName(code),
                    buy = latest.value,
                    sell = latest.value,
                    median = latest.value,
                    lastUpdated = "Local",
                    trend = Trend.STABLE,
                    iconResId = getCurrencyIcon(code)
                ))
            }
        }
        _cryptoRates.value = crypto
    }

    override suspend fun refreshRates() {
        try {
            val response = apiService.getHistorial()
            if (response.isSuccessful) {
                val data = response.body() ?: emptyList()
                
                val rawTimestamp = System.currentTimeMillis()
                val timestamp = rawTimestamp - (rawTimestamp % (60 * 60 * 1000))
                
                val mappedInformal = data.filter { coinRate ->
                    val code = mapCoinCode(coinRate.coin)
                    code in marketCodes
                }.map { coinRate ->
                    val code = mapCoinCode(coinRate.coin)
                    rateDao.insertRate(RateHistory(code = code, value = coinRate.last.value, timestamp = timestamp))
                    ExchangeRate(
                        code = code,
                        name = getCurrencyName(code),
                        buy = coinRate.last.value,
                        sell = coinRate.last.value,
                        median = coinRate.last.value,
                        lastUpdated = "Ahora",
                        trend = Trend.STABLE,
                        iconResId = getCurrencyIcon(code)
                    )
                }

                val mappedCrypto = data.filter { coinRate ->
                    val code = mapCoinCode(coinRate.coin)
                    code in cryptoCodes
                }.map { coinRate ->
                    val code = mapCoinCode(coinRate.coin)
                    rateDao.insertRate(RateHistory(code = code, value = coinRate.last.value, timestamp = timestamp))
                    ExchangeRate(
                        code = code,
                        name = getCurrencyName(code),
                        buy = coinRate.last.value,
                        sell = coinRate.last.value,
                        median = coinRate.last.value,
                        lastUpdated = "Ahora",
                        trend = Trend.STABLE,
                        iconResId = getCurrencyIcon(code)
                    )
                }

                _informalRates.value = mappedInformal
                _cryptoRates.value = mappedCrypto
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun refreshHistory(code: String, period: String) {
        try {
            val apiCode = when(code) {
                "ZELLE" -> "USD_ZELLE.CUP"
                "USDT" -> "USDT_TRC20"
                "ECU" -> "EUR"
                else -> code
            }

            val response = apiService.getHistorico(apiCode)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                    val historyPoints = body.data.mapNotNull { item ->
                        val date = dateFormat.parse(item.id)
                        if (date != null) {
                            RateHistory(code = code, value = item.median, timestamp = date.time)
                        } else null
                    }
                    rateDao.insertRates(historyPoints)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun syncAllHistory() {
        allRelevantCodes.forEach { code ->
            refreshHistory(code, "TODO")
        }
    }

    override suspend fun refreshMessages() {
        try {
            val response = apiService.getMessages()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val alerts = mutableListOf<SmsAlert>()
                    val timeString = String.format(Locale.US, "%02d:%02d", body.hour, body.minutes)
                    
                    body.statistics.forEach { (coin, stats) ->
                        val displayCode = mapCoinCode(coin)
                        stats.messages.forEach { msg ->
                            alerts.add(SmsAlert(title = "Alerta $displayCode", message = msg, time = timeString))
                        }
                    }
                    smsDao.insertSmsList(alerts)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun checkForUpdate(): UpdateInfo? {
        return try {
            val response = updateApi.getLatestRelease()
            if (response.isSuccessful) {
                val release = response.body() ?: return null
                val tag = release.tagName.replace(Regex("[^0-9]"), "")
                val versionCode = tag.toLongOrNull() ?: 0L
                val apkAsset = release.assets.find { it.name.endsWith(".apk") } ?: release.assets.firstOrNull()
                
                UpdateInfo(
                    versionName = release.name.ifBlank { release.tagName },
                    versionCode = versionCode,
                    releaseNotes = release.body,
                    apkUrl = apkAsset?.downloadUrl ?: ""
                )
            } else {
                android.util.Log.e("ExchangeRepository", "Error checking for updates: ${response.code()} ${response.message()}")
                null
            }
        } catch (e: Exception) {
            android.util.Log.e("ExchangeRepository", "Exception checking for updates", e)
            null
        }
    }

    private fun mapCoinCode(coin: String): String {
        return when(coin) {
            "USD_ZELLE.CUP" -> "ZELLE"
            "USDT_TRC20" -> "USDT"
            "EUR" -> "ECU"
            else -> coin
        }
    }

    private fun getCurrencyName(code: String): String {
        return when (code.uppercase()) {
            "USD" -> "Dólar Estadounidense"
            "EUR", "ECU" -> "Euro"
            "MLC" -> "MLC"
            "BTC" -> "Bitcoin"
            "USDT" -> "Tether"
            "BNB" -> "BNB"
            "TRX" -> "TRON"
            "ZELLE" -> "Zelle"
            "MXN" -> "Peso Mexicano"
            "CAD" -> "Dólar Canadiense"
            "CLA" -> "Clasica"
            else -> code
        }
    }

    private fun getCurrencyIcon(code: String): Int {
        return when (code.uppercase()) {
            "USD" -> R.drawable.ic_usd
            "EUR", "ECU" -> R.drawable.ic_ecu
            "MLC" -> R.drawable.ic_mlc
            "BTC" -> R.drawable.ic_btc
            "USDT" -> R.drawable.ic_usdt
            "BNB" -> R.drawable.ic_bnb
            "TRX" -> R.drawable.ic_trx
            "ZELLE" -> R.drawable.ic_zelle
            "MXN" -> R.drawable.ic_mxn
            "CAD" -> R.drawable.ic_cad
            "CLA" -> R.drawable.ic_cla
            else -> R.drawable.placeholder
        }
    }
}
