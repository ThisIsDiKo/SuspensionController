package com.dikoresearchsuspensioncontroller.feature_controller.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import javax.inject.Inject

private const val APPLICATION_SETTINGS_NAME = "application-settings"
val Context.applicationDataStore by preferencesDataStore(name = APPLICATION_SETTINGS_NAME)


class DataStoreManager @Inject constructor(context: Context) {
    private val appDataStore = context.applicationDataStore

    suspend fun setDeviceAddress(address: String){
        appDataStore.edit { settings ->
            settings[SettingsKeys.DEVICE_MAC_ADDRESS] = address
        }
    }
}