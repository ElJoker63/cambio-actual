package com.aewaredev.cambioactual.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SyncPreferences(private val context: Context) {
    companion object {
        private val LAST_SYNC_TIMESTAMP = longPreferencesKey("last_sync_timestamp")
    }

    val lastSyncTimestamp: Flow<Long> = context.dataStore.data
        .map { preferences ->
            preferences[LAST_SYNC_TIMESTAMP] ?: 0L
        }

    suspend fun saveSyncTimestamp(timestamp: Long) {
        context.dataStore.edit { preferences ->
            preferences[LAST_SYNC_TIMESTAMP] = timestamp
        }
    }
}
