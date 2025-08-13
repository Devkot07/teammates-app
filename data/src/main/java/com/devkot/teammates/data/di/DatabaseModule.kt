package com.devkot.teammates.data.di

import android.content.Context
import androidx.room.Room
import com.devkot.teammates.data.local.database.TeammatesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TeammatesDatabase {
        return Room.databaseBuilder(
            context,
            TeammatesDatabase::class.java,
            "teammates_database"
        ).build()
    }
}