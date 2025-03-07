package com.pezont.teammates.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.pezont.teammates.models.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException
import javax.inject.Inject


class UserDataRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val USER = stringPreferencesKey("user")
        const val TAG = "UserDataRepo"
    }

    val refreshToken: Flow<String> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { data -> data[REFRESH_TOKEN] ?: "0" }

    val accessToken: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences.", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences -> preferences[ACCESS_TOKEN] ?: "0" }

    val user: Flow<User> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences.", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val userJson = preferences[USER]
            if (userJson != null) {
                Json.decodeFromString<User>(userJson)
            } else {
                User()
            }
        }


    suspend fun saveAccessToken(accessToken: String) {
        dataStore.edit { data ->
            Log.i(TAG, "Save accessToken: $accessToken")
            data[ACCESS_TOKEN] = accessToken
        }
    }

    suspend fun saveRefreshToken(refreshToken: String) {
        dataStore.edit { data ->
            Log.i(TAG, "Save refreshToken: $refreshToken")
            data[REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun saveUser(user: User) {
        val userJson = Json.encodeToString(user)
        dataStore.edit { data ->
            Log.i(TAG, "Save userJson: $userJson")
            data[USER] = userJson
        }
    }


}


