package com.dikoresearchsuspensioncontroller.feature_controller.data.local

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object SettingsKeys {
    val DEVICE_MAC_ADDRESS = stringPreferencesKey("device_mac_address")
    val DEVICE_TYPE = stringPreferencesKey("device_type")
    val SHOW_PRESSURE_IN_TANK = booleanPreferencesKey("show_pressure_in_tank")
    val DEVICE_NAME = stringPreferencesKey("device_name")
    val DEVICE_FIRMWARE_VERSION = stringPreferencesKey("device_firmware_version")
    val DEVICE_MODE = stringPreferencesKey("device_mode")
    val PRESSURE_SENSOR = stringPreferencesKey("pressure_sensors")
    val PRESSURE_UNITS = stringPreferencesKey("pressure_units")

    val USE_CONTROL_GROUP = booleanPreferencesKey("use_control_group")
    val USE_PRESSURE_REGULATION = booleanPreferencesKey("use_pressure_regulation")
    val AIR_PREPARING_SYSTEM = stringPreferencesKey("air_preparing_system")
    val PRESSURE_SETUP_1 = stringPreferencesKey("pressure_setup_1")
    val PRESSURE_SETUP_2 = stringPreferencesKey("pressure_setup_2")
    val PRESSURE_SETUP_3 = stringPreferencesKey("pressure_setup_3")

}