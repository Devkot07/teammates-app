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
package com.pezont.teammates.data

import android.content.Context
import android.util.Log
import com.pezont.teammates.dummy.AuthDummyApiService
import com.pezont.teammates.dummy.NetworkUserDummyRepository
import com.pezont.teammates.dummy.UserDummyRepository
import com.pezont.teammates.network.TeammatesAuthApiService
import com.pezont.teammates.network.TeammatesQuestionnairesApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


interface AppContainer {
    val questionnairesRepository: QuestionnairesRepository
    val userDummyRepository: UserDummyRepository
    val authRepository: AuthRepository

}


class DefaultAppContainer(private val context: Context) : AppContainer {
    private val dummyUrl = "https://dummyjson.com/"
    private val ip = "192.168.139.235"
    // android.aapt2FromMavenOverride=/usr/bin/aapt2
    // cuddly-robot-pjqxjwp7g59c6495
    private val appIp = "obscure-space-carnival-7vrqpvv7jqwr275-"
    // TODO  ip


    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()


    private fun createRetrofit(baseUrl: String): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .baseUrl(baseUrl)
        .build()

    override val userDummyRepository: UserDummyRepository by lazy {
        val retrofitService = createRetrofit(dummyUrl).create(AuthDummyApiService::class.java)
        NetworkUserDummyRepository(retrofitService, context)
    }

    override val authRepository: AuthRepository by lazy {
        val retrofitService =
            createRetrofit(
                //"http://$ip:8100/"
                "https://${appIp}8100.app.github.dev"
            ).create(TeammatesAuthApiService::class.java)
        Log.i("LOGIC", "http://${appIp}8100.app.github.dev")
        NetworkAuthRepository(retrofitService, context)
    }

    override val questionnairesRepository: QuestionnairesRepository by lazy {
        val retrofitService =
            createRetrofit(
                //"http://$ip:8000/"
                "https://${appIp}8000.app.github.dev"
            ).create(TeammatesQuestionnairesApiService::class.java)
        NetworkQuestionnairesRepository(retrofitService, context)
    }


}
