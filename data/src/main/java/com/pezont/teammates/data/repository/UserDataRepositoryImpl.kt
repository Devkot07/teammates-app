package com.pezont.teammates.data.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.pezont.teammates.domain.model.User
import com.pezont.teammates.domain.repository.UserDataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject


class UserDataRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) :UserDataRepository {
    private companion object {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val USER_NICKNAME = stringPreferencesKey("user_nickname")
        val USER_PUBLIC_ID = stringPreferencesKey("user_public_id")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_DESCRIPTION = stringPreferencesKey("user_description")
        val USER_IMAGE_PATH = stringPreferencesKey("user_image_path")
        const val TAG = "UserDataRepo"
    }

    override val refreshToken: Flow<String> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { data -> data[REFRESH_TOKEN] ?: "0" }

    override val accessToken: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences.", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences -> preferences[ACCESS_TOKEN] ?: "0" }

    override val user: Flow<User> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences.", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val userNickname = preferences[USER_NICKNAME] ?: ""
            val userPublicId = preferences[USER_PUBLIC_ID] ?: ""
            val userEmail = preferences[USER_EMAIL] ?: ""
            val userDescription = preferences[USER_DESCRIPTION] ?: ""
            val userImagePath = preferences[USER_IMAGE_PATH] ?: ""

            User(
                nickname = userNickname,
                publicId = userPublicId,
                email = userEmail,
                description = userDescription,
                imagePath = userImagePath
            )
        }

    override suspend fun saveAccessToken(newAccessToken: String) {
        dataStore.edit { data ->
            Log.i(TAG, "Save accessToken: $newAccessToken")
            data[ACCESS_TOKEN] = newAccessToken
        }
    }

    override suspend fun saveRefreshToken(newRefreshToken: String) {
        dataStore.edit { data ->
            Log.i(TAG, "Save refreshToken: $newRefreshToken")
            data[REFRESH_TOKEN] = newRefreshToken
        }
    }

    override suspend fun saveUser(user: User) {
        dataStore.edit { data ->
            data[USER_NICKNAME] = user.nickname ?: ""
            data[USER_PUBLIC_ID] = user.publicId ?: ""
            data[USER_EMAIL] = user.email ?: ""
            data[USER_DESCRIPTION] = user.description ?: ""
            data[USER_IMAGE_PATH] = user.imagePath ?: ""
        }
    }
}
