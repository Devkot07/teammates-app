/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pezont.teammates.di

import android.content.Context
import android.util.Log
import com.pezont.teammates.BuildConfig
import com.pezont.teammates.data.TeammatesAuthApiService
import com.pezont.teammates.data.TeammatesQuestionnairesApiService
import com.pezont.teammates.data.TeammatesUsersApiService
import com.pezont.teammates.data.interceptor.TokenRefreshInterceptor
import com.pezont.teammates.data.repository.AuthRepositoryImpl
import com.pezont.teammates.data.repository.QuestionnairesRepositoryImpl
import com.pezont.teammates.data.repository.UsersRepositoryImpl
import com.pezont.teammates.domain.repository.AuthRepository
import com.pezont.teammates.domain.repository.QuestionnairesRepository
import com.pezont.teammates.domain.repository.UsersRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject


interface AppContainer {
    val questionnairesRepository: QuestionnairesRepository
    val authRepository: AuthRepository
    val usersRepository: UsersRepository
}


class DefaultAppContainer @Inject constructor(
    private val context: Context,
    tokenRefreshInterceptor: TokenRefreshInterceptor
) : AppContainer {

    private fun createBaseOkHttpClientBuilder(): OkHttpClient.Builder {
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            if (message.isNotBlank() &&
                (!message.contains(":", ignoreCase = true)
                        || message.trim().startsWith("<")
                        || message.trim().startsWith("{")
                        || message.trim().startsWith("["))
            ) {
                Log.i("OkHttp", message)
            }
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }


        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .followRedirects(false)
            .followSslRedirects(false)
    }

    private val okHttpClient = createBaseOkHttpClientBuilder()
        .addInterceptor(tokenRefreshInterceptor)
        .build()

    private val authOkHttpClient = createBaseOkHttpClientBuilder().build()


    private fun createRetrofit(baseUrl: String, client: OkHttpClient): Retrofit {
        Log.d(
            "AppContainer",
            "Creating retrofit with client: ${client.hashCode()}, isAuthClient: ${client == authOkHttpClient}"
        )
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .baseUrl(baseUrl)
            .build()
    }


    override val authRepository: AuthRepository by lazy {
        val retrofitService =
            createRetrofit(
                "${BuildConfig.BASE_URL}${BuildConfig.PORT_2}${BuildConfig.END_URL}",
                authOkHttpClient
            )
                .create(TeammatesAuthApiService::class.java)
        AuthRepositoryImpl(retrofitService, context)
    }

    override val questionnairesRepository: QuestionnairesRepository by lazy {
        val retrofitService =
            createRetrofit(
                "${BuildConfig.BASE_URL}${BuildConfig.PORT_1}${BuildConfig.END_URL}",
                okHttpClient
            )

                .create(TeammatesQuestionnairesApiService::class.java)
        QuestionnairesRepositoryImpl(retrofitService, context)
    }

    override val usersRepository: UsersRepository by lazy {
        val retrofitService =
            createRetrofit(
                "${BuildConfig.BASE_URL}${BuildConfig.PORT_3}${BuildConfig.END_URL}",
                okHttpClient
            )
                .create(TeammatesUsersApiService::class.java)
        UsersRepositoryImpl(retrofitService, context)
    }


}
