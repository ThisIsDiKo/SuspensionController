package com.dikoresearchsuspensioncontroller.feature_controller.di

import android.content.Context
import com.dikoresearchsuspensioncontroller.feature_controller.data.ble.BleManager
import com.dikoresearchsuspensioncontroller.feature_controller.data.ble.SuspensionControllerRepositoryImpl
import com.dikoresearchsuspensioncontroller.feature_controller.data.local.DataStoreRepositoryImpl
import com.dikoresearchsuspensioncontroller.feature_controller.domain.repository.ble.SuspensionControllerRepository
import com.dikoresearchsuspensioncontroller.feature_controller.domain.repository.local.DataStoreRepository
import com.dikoresearchsuspensioncontroller.feature_controller.domain.usecases.suspensioncontroller.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FeatureControllerModule {
    @Provides
    @Singleton
    fun provideBleManager(@ApplicationContext appContext: Context): BleManager{
        return BleManager(appContext)
    }

    @Provides
    @Singleton
    fun provideSuspensionControllerRepository(bleManager: BleManager): SuspensionControllerRepository{
        return SuspensionControllerRepositoryImpl(bleManager)
    }

    @Provides
    @Singleton
    fun provideDataStoreRepository(@ApplicationContext appContext: Context): DataStoreRepository{
        return DataStoreRepositoryImpl(appContext)
    }

    @Provides
    @Singleton
    fun provideDKControllerUseCases(suspensionController: SuspensionControllerRepository): SuspensionControllerUseCases{
        return SuspensionControllerUseCases(
            connectToPeripheral = ConnectToPeripheral(suspensionController),
            disconnectFromPeripheral = DisconnectFromPeripheral(suspensionController),
            readSensorsValues = ReadSensorsValues(suspensionController),
            writeOutputs = WriteOutputs(suspensionController),
            startScanForPeripherals = StartScanForPeripherals(suspensionController),
            stopScanForPeripherals = StopScanForPeripherals(suspensionController),
            requestMtuFromPeripheral = RequestMtuFromPeripheral(suspensionController),
            setConnectionStatusObserver = SetConnectionStatusObserver(suspensionController),
            autoConnectPeripheral = AutoConnectPeripheral(suspensionController)
        )
    }
}