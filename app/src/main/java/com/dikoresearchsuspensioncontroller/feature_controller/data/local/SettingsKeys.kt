package com.dikoresearchsuspensioncontroller.feature_controller.data.local

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object SettingsKeys {
    val DEVICE_MAC_ADDRESS = stringPreferencesKey("device_mac_address")
    val DEVICE_TYPE = stringPreferencesKey("device_type")
    val SHOW_PRESSURE_IN_TANK = booleanPreferencesKey("show_pressure_in_tank")
}