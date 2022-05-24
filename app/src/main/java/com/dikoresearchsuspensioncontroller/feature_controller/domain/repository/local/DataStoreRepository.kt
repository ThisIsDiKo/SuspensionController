package com.dikoresearchsuspensioncontroller.feature_controller.domain.repository.local

import com.dikoresearchsuspensioncontroller.feature_controller.domain.model.*
import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    suspend fun setDeviceAddress(address: String)
    suspend fun setDeviceName(name: String)
    suspend fun setDeviceType(type: DeviceType)
    suspend fun setDeviceFirmwareVersion(version: String)
    suspend fun setDeviceMode(mode: DeviceMode)
    suspend fun setUseTankPressure(useTankPressure: Boolean)
    suspend fun setPressureSensor(sensor: PressureSensor)
    suspend fun setPressureUnits(units: PressureUnits)

    fun getApplicationSettingsFlow(): Flow<ApplicationSettings>
}