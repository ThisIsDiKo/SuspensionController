package com.dikoresearchsuspensioncontroller.feature_controller.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.dikoresearchsuspensioncontroller.feature_controller.domain.model.*
import com.dikoresearchsuspensioncontroller.feature_controller.domain.repository.local.DataStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

private const val APPLICATION_SETTINGS_NAME = "application-settings"
val Context.applicationDataStore by preferencesDataStore(name = APPLICATION_SETTINGS_NAME)


class DataStoreRepositoryImpl @Inject constructor(context: Context): DataStoreRepository {
    private val appDataStore = context.applicationDataStore

    override suspend fun setDeviceAddress(address: String){
        appDataStore.edit { settings ->
            settings[SettingsKeys.DEVICE_MAC_ADDRESS] = address
        }
    }

    override suspend fun setDeviceName(name: String) {
        appDataStore.edit { settings ->
            settings[SettingsKeys.DEVICE_NAME] = name
        }
    }

    override suspend fun setDeviceType(type: DeviceType) {
        appDataStore.edit { settings ->
            settings[SettingsKeys.DEVICE_TYPE] = type.alias
        }
    }

    override suspend fun setDeviceFirmwareVersion(version: String) {
        appDataStore.edit { settings ->
            settings[SettingsKeys.DEVICE_FIRMWARE_VERSION] = version
        }
    }

    override suspend fun setDeviceMode(mode: DeviceMode) {
        appDataStore.edit { settings ->
            settings[SettingsKeys.DEVICE_MODE] = mode.alias
        }
    }

    override suspend fun setUseTankPressure(useTankPressure: Boolean) {
        appDataStore.edit { settings ->
            settings[SettingsKeys.SHOW_PRESSURE_IN_TANK] = useTankPressure
        }
    }

    override suspend fun setPressureSensor(sensor: PressureSensor) {
        appDataStore.edit { settings ->
            settings[SettingsKeys.PRESSURE_SENSOR] = sensor.alias
        }
    }

    override suspend fun setPressureUnits(units: PressureUnits) {
        appDataStore.edit { settings ->
            settings[SettingsKeys.PRESSURE_UNITS] = units.alias
        }
    }

    override suspend fun setShowControlGroup(use: Boolean) {
        appDataStore.edit { settings ->
            settings[SettingsKeys.USE_CONTROL_GROUP] = use
        }
    }

    override suspend fun setShowRegulationGroup(use: Boolean) {
        appDataStore.edit { settings ->
            settings[SettingsKeys.USE_PRESSURE_REGULATION] = use
        }
    }

    override suspend fun setAirPreparingSystem(airPreparingSystem: AirPreparingSystem) {
        appDataStore.edit { settings ->
            settings[SettingsKeys.AIR_PREPARING_SYSTEM] = airPreparingSystem.alias
        }
    }

    override suspend fun setPressurePreset(presetNum: Int, preset: String) {
        when(presetNum){
            1 -> {
                appDataStore.edit { settings ->
                    settings[SettingsKeys.PRESSURE_SETUP_1] = preset
                }
            }
            2 -> {
                appDataStore.edit { settings ->
                    settings[SettingsKeys.PRESSURE_SETUP_2] = preset
                }
            }
            3 -> {
                appDataStore.edit { settings ->
                    settings[SettingsKeys.PRESSURE_SETUP_3] = preset
                }
            }
            else ->{
                Timber.e("Unknown preset Number")
            }
        }
    }

    override fun getApplicationSettingsFlow(): Flow<ApplicationSettings> = appDataStore.data
        .catch { exception ->
            throw exception
        }
        .map { settings ->
            val deviceAddress = settings[SettingsKeys.DEVICE_MAC_ADDRESS] ?: ""
            val deviceName = settings[SettingsKeys.DEVICE_NAME] ?: ""
            val deviceFirmwareVersion = settings[SettingsKeys.DEVICE_FIRMWARE_VERSION] ?: ""

            val deviceType = when(settings[SettingsKeys.DEVICE_TYPE]){
                DeviceType.SimplePressure().alias -> DeviceType.SimplePressure()
                DeviceType.QuadroPressure().alias -> DeviceType.QuadroPressure()
                else -> DeviceType.SimplePressure()
            }

            val deviceMode = when(settings[SettingsKeys.DEVICE_MODE]){
                DeviceMode.SingleWay().alias -> DeviceMode.SingleWay()
                DeviceMode.DoubleWay().alias -> DeviceMode.DoubleWay()
                DeviceMode.QuadroWay().alias -> DeviceMode.QuadroWay()
                else -> DeviceMode.DoubleWay()
            }

            val useTankPressure = settings[SettingsKeys.SHOW_PRESSURE_IN_TANK] ?: false

            val pressureSensor = when(settings[SettingsKeys.PRESSURE_SENSOR]){
                PressureSensor.China_0_20().alias -> PressureSensor.China_0_20()
                PressureSensor.Catterpillar().alias -> PressureSensor.Catterpillar()
                else -> PressureSensor.China_0_20()
            }

            val pressureUnits = when(settings[SettingsKeys.PRESSURE_UNITS]){
                PressureUnits.Bar().alias -> PressureUnits.Bar()
                PressureUnits.Psi().alias -> PressureUnits.Psi()
                else -> PressureUnits.Bar()
            }

            val airPreparingSystem = when(settings[SettingsKeys.AIR_PREPARING_SYSTEM]){
                AirPreparingSystem.CompressorSystem().alias -> AirPreparingSystem.CompressorSystem()
                AirPreparingSystem.ReceiverSystem().alias -> AirPreparingSystem.ReceiverSystem()
                else -> AirPreparingSystem.ReceiverSystem()
            }

            val useControlGroup = settings[SettingsKeys.USE_CONTROL_GROUP] ?: true
            val usePressureRegulation = settings[SettingsKeys.USE_PRESSURE_REGULATION] ?: false

            val pressurePreset1 = settings[SettingsKeys.PRESSURE_SETUP_1] ?: "0,0,0,0"
            val pressurePreset2 = settings[SettingsKeys.PRESSURE_SETUP_2] ?: "0,0,0,0"
            val pressurePreset3 = settings[SettingsKeys.PRESSURE_SETUP_3] ?: "0,0,0,0"

           ApplicationSettings(
               deviceAddress = deviceAddress,
               deviceName = deviceName,
               deviceType = deviceType,
               deviceFirmwareVersion = deviceFirmwareVersion,
               deviceMode = deviceMode,
               useTankPressure = useTankPressure,
               pressureSensorType = pressureSensor,
               pressureUnits = pressureUnits,
               showControlGroup = useControlGroup,
               showRegulationGroup = usePressureRegulation,
               airPreparingSystem = airPreparingSystem,
               pressurePreset1 = pressurePreset1,
               pressurePreset2 = pressurePreset2,
               pressurePreset3 = pressurePreset3,
           )
        }


}