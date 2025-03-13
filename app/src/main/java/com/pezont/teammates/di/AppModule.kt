package com.pezont.teammates.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.pezont.teammates.data.repository.UserDataRepositoryImpl
import com.pezont.teammates.domain.repository.AuthRepository
import com.pezont.teammates.domain.repository.QuestionnairesRepository
import com.pezont.teammates.domain.repository.UserDataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val LAYOUT_PREFERENCE_NAME = "layout_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = LAYOUT_PREFERENCE_NAME
)


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
    fun provideAppContainer(@ApplicationContext context: Context): AppContainer {
        return DefaultAppContainer(context)
    }

    @Provides
    @Singleton
    fun provideQuestionnairesRepository(appContainer: AppContainer): QuestionnairesRepository {
        return appContainer.questionnairesRepository
    }

    @Provides
    @Singleton
    fun provideAuthRepository(appContainer: AppContainer): AuthRepository {
        return appContainer.authRepository
    }
}