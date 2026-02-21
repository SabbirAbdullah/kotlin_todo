package com.example.taskmanager.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("jwt_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
    }

    val tokenFlow: Flow<String?> = context.dataStore.data.map { it[TOKEN_KEY].toString() }

    val isLoggedInFlow: Flow<Boolean> = context.dataStore.data.map { it[TOKEN_KEY] != null }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { it[TOKEN_KEY] = token }
    }

    suspend fun saveRefreshToken(token: String) {
        context.dataStore.edit { it[REFRESH_TOKEN_KEY] = token }
    }

    suspend fun saveUserInfo(id: Int, name: String, email: String) {
        context.dataStore.edit {
            it[USER_ID_KEY] = id.toString()
            it[USER_NAME_KEY] = name
            it[USER_EMAIL_KEY] = email
        }
    }

    fun getTokenSync(): String? = runBlocking {
        context.dataStore.data.first()[TOKEN_KEY]
    }

    suspend fun getToken(): String? = context.dataStore.data.first()[TOKEN_KEY]

    suspend fun getRefreshToken(): String? = context.dataStore.data.first()[REFRESH_TOKEN_KEY]

    suspend fun getUserName(): String? = context.dataStore.data.first()[USER_NAME_KEY]

    suspend fun getUserEmail(): String? = context.dataStore.data.first()[USER_EMAIL_KEY]

    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}