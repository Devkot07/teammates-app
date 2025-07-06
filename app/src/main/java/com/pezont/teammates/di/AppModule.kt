package com.pezont.teammates.di

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.pezont.teammates.BuildConfig
import com.pezont.teammates.data.api.TeammatesAuthApiService
import com.pezont.teammates.data.api.TeammatesQuestionnairesApiService
import com.pezont.teammates.data.api.TeammatesUsersApiService
import com.pezont.teammates.data.interceptor.TokenRefreshInterceptor
import com.pezont.teammates.data.repository.AuthRepositoryImpl
import com.pezont.teammates.data.repository.QuestionnairesRepositoryImpl
import com.pezont.teammates.data.repository.UserDataRepositoryImpl
import com.pezont.teammates.data.repository.UsersRepositoryImpl
import com.pezont.teammates.domain.repository.AuthRepository
import com.pezont.teammates.domain.repository.QuestionnairesRepository
import com.pezont.teammates.domain.repository.UserDataRepository
import com.pezont.teammates.domain.repository.UsersRepository
import com.pezont.teammates.domain.usecase.UpdateTokensUseCase
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

private const val LAYOUT_PREFERENCE_NAME = "layout_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = LAYOUT_PREFERENCE_NAME
)

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RegularClient

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun provideUserDataRepository(dataStore: DataStore<Preferences>): UserDataRepository {
        return UserDataRepositoryImpl(dataStore)
    }

    @Provides
    @Singleton
    fun provideTokenRefreshInterceptor(
        userDataRepository: UserDataRepository,
        updateTokensUseCase: Lazy<UpdateTokensUseCase>
    ): TokenRefreshInterceptor {
        return TokenRefreshInterceptor(userDataRepository, updateTokensUseCase)
    }

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
                Log.i("OkHttp", message)
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


    @Provides
    @Singleton
    fun provideAuthRepository(
        apiService: TeammatesAuthApiService,
        @ApplicationContext context: Context
    ): AuthRepository {
        return AuthRepositoryImpl(apiService, context)
    }
    @Provides
    @Singleton
    fun provideQuestionnairesRepository(
        apiService: TeammatesQuestionnairesApiService,
        @ApplicationContext context: Context
    ): QuestionnairesRepository {
        return QuestionnairesRepositoryImpl(apiService, context)
    }
    @Provides
    @Singleton
    fun provideUsersRepository(
        apiService: TeammatesUsersApiService,
        @ApplicationContext context: Context
    ): UsersRepository {
        return UsersRepositoryImpl(apiService, context)
    }
}