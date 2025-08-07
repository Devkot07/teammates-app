package com.devkot.teammates.di.module

import android.content.Context
import coil.ImageLoader
import com.devkot.teammates.data.remote.api.TeammatesAuthApiService
import com.devkot.teammates.data.remote.api.TeammatesQuestionnairesApiService
import com.devkot.teammates.data.remote.api.TeammatesUsersApiService
import com.devkot.teammates.data.repository.AuthRepositoryImpl
import com.devkot.teammates.data.repository.ImageRepositoryImpl
import com.devkot.teammates.data.repository.QuestionnairesRepositoryImpl
import com.devkot.teammates.data.repository.UsersRepositoryImpl
import com.devkot.teammates.domain.repository.AuthRepository
import com.devkot.teammates.domain.repository.ImageRepository
import com.devkot.teammates.domain.repository.QuestionnairesRepository
import com.devkot.teammates.domain.repository.UsersRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

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

    @Provides
    @Singleton
    fun provideImageRepository(
        imageLoader: ImageLoader,
        @ApplicationContext context: Context
    ): ImageRepository {
        return ImageRepositoryImpl(context, imageLoader)
    }
}