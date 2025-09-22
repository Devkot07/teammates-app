package com.devkot.teammates.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.devkot.teammates.domain.model.User
import com.devkot.teammates.domain.repository.UserDataRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject


class UserDataRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : UserDataRepository {
    private companion object {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val USER_NICKNAME = stringPreferencesKey("user_nickname")
        val USER_PUBLIC_ID = stringPreferencesKey("user_public_id")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_DESCRIPTION = stringPreferencesKey("user_description")
        val USER_IMAGE_PATH = stringPreferencesKey("user_image_path")
        val REFRESH_TOKEN_EXPIRATION_TIME = longPreferencesKey("REFRESH_TOKEN_EXPIRATION_TIME ")

    }


    override suspend fun user() : User = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
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
        }.first()

    override suspend fun accessToken(): String = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences -> preferences[ACCESS_TOKEN] ?: "0" }.first()

    override suspend fun refreshToken(): String = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences -> preferences[REFRESH_TOKEN] ?: "" }.first()

    override suspend fun refreshTokenExpirationTime(): Long = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences -> preferences[REFRESH_TOKEN_EXPIRATION_TIME] ?: 0L }.first()

    override suspend fun saveUser(user: User) {
        dataStore.edit { data ->
            data[USER_NICKNAME] = user.nickname
            data[USER_PUBLIC_ID] = user.publicId
            data[USER_EMAIL] = user.email
            data[USER_DESCRIPTION] = user.description
            data[USER_IMAGE_PATH] = user.imagePath
        }
    }

    override suspend fun saveAccessToken(newAccessToken: String) {
        dataStore.edit { data ->
            data[ACCESS_TOKEN] = newAccessToken
        }
    }

    override suspend fun saveRefreshToken(newRefreshToken: String) {
        dataStore.edit { data ->
            data[REFRESH_TOKEN] = newRefreshToken
        }
    }


    override suspend fun updateUserProfile(
        nickname: String?,
        description: String?,
        imagePath: String?
    ) {
        dataStore.edit { data ->
            nickname?.let {
                data[USER_NICKNAME] = it
            }
            description?.let {
                data[USER_DESCRIPTION] = it
            }
            imagePath?.let {
                data[USER_IMAGE_PATH] = it
            }
        }
    }

    override suspend fun saveRefreshTokenExpirationTime(refreshTokenExpirationTime: Long) {
        dataStore.edit { prefs ->
            prefs[REFRESH_TOKEN_EXPIRATION_TIME] = refreshTokenExpirationTime
        }
    }

}
