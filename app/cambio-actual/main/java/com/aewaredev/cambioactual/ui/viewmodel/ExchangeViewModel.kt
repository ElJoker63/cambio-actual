package com.aewaredev.cambioactual.ui.viewmodel

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aewaredev.cambioactual.data.model.*
import com.aewaredev.cambioactual.data.preferences.SyncPreferences
import com.aewaredev.cambioactual.data.preferences.ThemePreferences
import com.aewaredev.cambioactual.data.repository.ExchangeRepository
import com.aewaredev.cambioactual.data.repository.ExchangeRepositoryImpl
import androidx.glance.appwidget.updateAll
import com.aewaredev.cambioactual.ui.widget.ExchangeWidget
import com.aewaredev.cambioactual.ui.widget.QuickWidget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.Calendar

class ExchangeViewModel(
    application: Application,
    private val repository: ExchangeRepository
) : AndroidViewModel(application) {

    private val themePreferences = ThemePreferences(application)
    private val syncPreferences = SyncPreferences(application)

    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _pinnedCodes = MutableStateFlow(setOf("USD", "EUR", "MLC"))
    val pinnedCodes: StateFlow<Set<String>> = _pinnedCodes

    private val _selectedCurrency = MutableStateFlow("USD")
    val selectedCurrency: StateFlow<String> = _selectedCurrency

    private val _selectedPeriod = MutableStateFlow("1S")
    val selectedPeriod: StateFlow<String> = _selectedPeriod

    private val _updateInfo = MutableStateFlow<UpdateInfo?>(null)
    val updateInfo: StateFlow<UpdateInfo?> = _updateInfo

    private val _isDownloadingUpdate = MutableStateFlow(false)
    val isDownloadingUpdate: StateFlow<Boolean> = _isDownloadingUpdate

    private val _initialSyncInProgress = MutableStateFlow(false)
    val initialSyncInProgress: StateFlow<Boolean> = _initialSyncInProgress

    val informalRates: StateFlow<List<ExchangeRate>> = repository.getInformalRates()
        .combine(_searchQuery) { rates, query ->
            filterRates(rates, query)
        }
        .combine(_pinnedCodes) { filteredRates, pinned ->
            filteredRates.sortedByDescending { it.code in pinned }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val cryptoRates: StateFlow<List<ExchangeRate>> = repository.getCryptoRates()
        .combine(_searchQuery) { rates, query ->
            filterRates(rates, query)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val smsAlerts: StateFlow<List<SmsAlert>> = repository.getSmsAlerts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val historyData: StateFlow<List<RateHistory>> = combine(_selectedCurrency, _selectedPeriod) { coin, period ->
        Pair(coin, period)
    }.flatMapLatest { (coin, period) ->
        val since = calculateSinceTimestamp(period)
        repository.getRateHistorySince(coin, since)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        loadTheme()
        checkForUpdates()
        
        viewModelScope.launch {
            // Load local data first for instant display
            (repository as? ExchangeRepositoryImpl)?.loadLocalData()
            refresh()
        }
    }

    private fun loadTheme() {
        themePreferences.isDarkTheme
            .onEach { _isDarkTheme.value = it }
            .launchIn(viewModelScope)
    }

    private fun checkForUpdates() {
        viewModelScope.launch {
            android.util.Log.d("ExchangeViewModel", "Checking for updates...")
            val info = repository.checkForUpdate()
            if (info != null) {
                android.util.Log.d("ExchangeViewModel", "Update info found: v${info.versionName} (${info.versionCode})")
                if (info.versionCode > getCurrentVersionCode()) {
                    android.util.Log.d("ExchangeViewModel", "New version available!")
                    _updateInfo.value = info
                } else {
                    android.util.Log.d("ExchangeViewModel", "Already on latest version")
                }
            } else {
                android.util.Log.d("ExchangeViewModel", "No update info returned")
            }
        }
    }

    private fun getCurrentVersionCode(): Long {
        val packageInfo = getApplication<Application>().packageManager.getPackageInfo(getApplication<Application>().packageName, 0)
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            packageInfo.longVersionCode
        } else {
            @Suppress("DEPRECATION")
            packageInfo.versionCode.toLong()
        }
    }

    fun downloadAndInstallUpdate(info: UpdateInfo) {
        viewModelScope.launch {
            _isDownloadingUpdate.value = true
            val file = withContext(Dispatchers.IO) {
                downloadFile(info.apkUrl, "update.apk")
            }
            _isDownloadingUpdate.value = false
            if (file != null) {
                installApk(file)
            }
        }
    }

    private fun downloadFile(urlStr: String, fileName: String): File? {
        return try {
            val context = getApplication<Application>()
            val updateDir = File(context.cacheDir, "updates")
            updateDir.mkdirs()
            val outputFile = File(updateDir, fileName)
            
            val url = URL(urlStr)
            val connection = url.openConnection() as HttpURLConnection
            connection.connect()
            
            if (connection.responseCode != HttpURLConnection.HTTP_OK) return null
            
            connection.inputStream.use { input ->
                FileOutputStream(outputFile).use { output ->
                    input.copyTo(output)
                }
            }
            outputFile
        } catch (e: Exception) {
            null
        }
    }

    private fun installApk(file: File) {
        val context = getApplication<Application>()
        val apkUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun toggleTheme() {
        viewModelScope.launch {
            val nextTheme = !_isDarkTheme.value
            _isDarkTheme.value = nextTheme
            themePreferences.saveTheme(nextTheme)
            ExchangeWidget().updateAll(getApplication())
            QuickWidget().updateAll(getApplication())
        }
    }

    fun selectCurrency(code: String) {
        _selectedCurrency.value = code
    }

    fun selectPeriod(period: String) {
        _selectedPeriod.value = period
    }

    private fun calculateSinceTimestamp(period: String): Long {
        val calendar = Calendar.getInstance()
        return when (period) {
            "1D" -> {
                calendar.add(Calendar.DAY_OF_YEAR, -2)
                calendar.timeInMillis
            }
            "1S" -> {
                calendar.add(Calendar.WEEK_OF_YEAR, -1)
                calendar.timeInMillis
            }
            "3S" -> {
                calendar.add(Calendar.WEEK_OF_YEAR, -3)
                calendar.timeInMillis
            }
            "3M" -> {
                calendar.add(Calendar.MONTH, -3)
                calendar.timeInMillis
            }
            "6M" -> {
                calendar.add(Calendar.MONTH, -6)
                calendar.timeInMillis
            }
            "TODO" -> 0L
            else -> {
                calendar.add(Calendar.WEEK_OF_YEAR, -1)
                calendar.timeInMillis
            }
        }
    }

    private fun filterRates(rates: List<ExchangeRate>, query: String): List<ExchangeRate> {
        return if (query.isBlank()) rates
        else rates.filter {
            it.code.contains(query, ignoreCase = true) ||
            it.name.contains(query, ignoreCase = true)
        }
    }

    fun getRateByCode(code: String): ExchangeRate? {
        return informalRates.value.find { it.code == code } 
            ?: cryptoRates.value.find { it.code == code }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun togglePin(code: String) {
        _pinnedCodes.value = if (code in _pinnedCodes.value) {
            _pinnedCodes.value - code
        } else {
            _pinnedCodes.value + code
        }
    }

    fun refresh() {
        viewModelScope.launch {
            val lastSync = syncPreferences.lastSyncTimestamp.first()
            val now = System.currentTimeMillis()
            val twelveHours = 12 * 60 * 60 * 1000L

            if (now - lastSync > twelveHours) {
                _initialSyncInProgress.value = true
                try {
                    repository.refreshRates()
                    repository.refreshMessages()
                    (repository as? ExchangeRepositoryImpl)?.syncAllHistory()
                    syncPreferences.saveSyncTimestamp(now)
                    ExchangeWidget().updateAll(getApplication<Application>())
                    QuickWidget().updateAll(getApplication<Application>())
                } finally {
                    _initialSyncInProgress.value = false
                }
            } else {
                // If within 12h, ensure local data is loaded (it should be already by init)
                (repository as? ExchangeRepositoryImpl)?.loadLocalData()
            }
        }
    }
}
