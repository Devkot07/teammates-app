package com.pezont.teammates

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.pezont.teammates.data.AppContainer
import com.pezont.teammates.data.DefaultAppContainer
import com.pezont.teammates.data.UserDataRepository


private const val LAYOUT_PREFERENCE_NAME = "layout_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = LAYOUT_PREFERENCE_NAME
)


class TeammatesApplication : Application() {
    lateinit var userDataRepository: UserDataRepository
    lateinit var container: AppContainer


    override fun onCreate() {
        super.onCreate()
        userDataRepository = UserDataRepository(dataStore)
        container = DefaultAppContainer(applicationContext)


    }


}