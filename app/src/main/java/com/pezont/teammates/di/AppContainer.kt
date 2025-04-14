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
import com.pezont.teammates.BuildConfig
import com.pezont.teammates.data.repository.AuthRepositoryImpl
import com.pezont.teammates.data.repository.QuestionnairesRepositoryImpl
import com.pezont.teammates.domain.repository.AuthRepository
import com.pezont.teammates.domain.repository.QuestionnairesRepository
import com.pezont.teammates.data.TeammatesAuthApiService
import com.pezont.teammates.data.TeammatesQuestionnairesApiService
import com.pezont.teammates.data.TeammatesUsersApiService
import com.pezont.teammates.data.repository.UsersRepositoryImpl
import com.pezont.teammates.domain.repository.UsersRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


interface AppContainer {
    val questionnairesRepository: QuestionnairesRepository
    val authRepository: AuthRepository
    val usersRepository: UsersRepository
}


class DefaultAppContainer(private val context: Context) : AppContainer {

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .followRedirects(false)
        .followSslRedirects(false)
        .build()


    private fun createRetrofit(baseUrl: String): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .baseUrl(baseUrl)
        .build()


    override val authRepository: AuthRepository by lazy {
        val retrofitService =
            createRetrofit("${BuildConfig.BASE_URL}${BuildConfig.PORT_2}${BuildConfig.END_URL}")
                .create(TeammatesAuthApiService::class.java)
        AuthRepositoryImpl(retrofitService, context)
    }

    override val questionnairesRepository: QuestionnairesRepository by lazy {
        val retrofitService =
            createRetrofit("${BuildConfig.BASE_URL}${BuildConfig.PORT_1}${BuildConfig.END_URL}")
                .create(TeammatesQuestionnairesApiService::class.java)
        QuestionnairesRepositoryImpl(retrofitService, context)
    }

    override val usersRepository: UsersRepository by lazy {
        val retrofitService =
            createRetrofit("${BuildConfig.BASE_URL}${BuildConfig.PORT_3}${BuildConfig.END_URL}")
                .create(TeammatesUsersApiService::class.java)
        UsersRepositoryImpl(retrofitService, context)
    }


}
