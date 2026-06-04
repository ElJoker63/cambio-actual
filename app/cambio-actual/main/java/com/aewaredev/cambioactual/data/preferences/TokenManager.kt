package com.aewaredev.cambioactual.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

class TokenManager(private val context: Context) {

    companion object {
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val USER_ID = intPreferencesKey("user_id")
        private val EMAIL = stringPreferencesKey("email")
        private val USERNAME = stringPreferencesKey("username")
        private val IS_VERIFIED = booleanPreferencesKey("is_verified")
        private val IS_VIP = booleanPreferencesKey("is_vip")
    }

    val accessToken: Flow<String?> = context.authDataStore.data.map { it[ACCESS_TOKEN] }
    val userId: Flow<Int?> = context.authDataStore.data.map { it[USER_ID] }
    val email: Flow<String?> = context.authDataStore.data.map { it[EMAIL] }
    val username: Flow<String?> = context.authDataStore.data.map { it[USERNAME] }
    val isVerified: Flow<Boolean> = context.authDataStore.data.map { it[IS_VERIFIED] ?: false }
    val isVip: Flow<Boolean> = context.authDataStore.data.map { it[IS_VIP] ?: false }

    suspend fun saveAuthData(
        token: String,
        userId: Int,
        email: String?,
        username: String?,
        isVerified: Boolean,
        isVip: Boolean
    ) {
        context.authDataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = token
            preferences[USER_ID] = userId
            email?.let { preferences[EMAIL] = it }
            username?.let { preferences[USERNAME] = it }
            preferences[IS_VERIFIED] = isVerified
            preferences[IS_VIP] = isVip
        }
    }

    suspend fun clearAuthData() {
        context.authDataStore.edit { it.clear() }
    }
}
