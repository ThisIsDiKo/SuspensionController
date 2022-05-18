package com.dikoresearchsuspensioncontroller.feature_controller.domain.repository.local

import com.dikoresearchsuspensioncontroller.feature_controller.domain.model.ApplicationSettings
import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    suspend fun setDeviceAddress(address: String)
    suspend fun setDeviceType()
    suspend fun setDeviceName()
    suspend fun setDeviceFirmwareVersion()
    suspend fun setNumOfWays()
    suspend fun setPressureUnits()
    suspend fun setPressureSensor()

    fun getApplicationSettingsFlow(): Flow<ApplicationSettings>
}