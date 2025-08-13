package com.devkot.teammates.di.module

import android.util.Log
import com.devkot.teammates.BuildConfig
import com.devkot.teammates.data.remote.api.TeammatesAuthApiService
import com.devkot.teammates.data.remote.api.TeammatesQuestionnairesApiService
import com.devkot.teammates.data.remote.api.TeammatesUsersApiService
import com.devkot.teammates.data.remote.interceptor.TokenRefreshInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RegularClient

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor { message ->
            if (message.isNotBlank() &&
                (!message.contains(":", ignoreCase = true) ||
                        message.trim().startsWith("<") ||
                        message.trim().startsWith("{") ||
                        message.trim().startsWith("["))
            ) {
                Log.d("OkHttp", message)
            }
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    @AuthClient
    fun provideAuthOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .followRedirects(false)
            .followSslRedirects(false)
            .build()
    }

    @Provides
    @Singleton
    @RegularClient
    fun provideRegularOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        tokenRefreshInterceptor: TokenRefreshInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(tokenRefreshInterceptor)
            .followRedirects(false)
            .followSslRedirects(false)
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApiService(@AuthClient client: OkHttpClient): TeammatesAuthApiService {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .baseUrl("${BuildConfig.BASE_URL}${BuildConfig.PORT_2}${BuildConfig.END_URL}")
            .build()
            .create(TeammatesAuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideQuestionnairesApiService(@RegularClient client: OkHttpClient): TeammatesQuestionnairesApiService {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .baseUrl("${BuildConfig.BASE_URL}${BuildConfig.PORT_1}${BuildConfig.END_URL}")
            .build()
            .create(TeammatesQuestionnairesApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideUsersApiService(@RegularClient client: OkHttpClient): TeammatesUsersApiService {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .baseUrl("${BuildConfig.BASE_URL}${BuildConfig.PORT_3}${BuildConfig.END_URL}")
            .build()
            .create(TeammatesUsersApiService::class.java)
    }
}