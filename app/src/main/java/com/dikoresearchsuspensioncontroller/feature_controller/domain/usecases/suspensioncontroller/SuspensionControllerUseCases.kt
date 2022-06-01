package com.dikoresearchsuspensioncontroller.feature_controller.domain.usecases.suspensioncontroller

data class SuspensionControllerUseCases(
    val connectToPeripheral: ConnectToPeripheral,
    val disconnectFromPeripheral: DisconnectFromPeripheral,
    val readSensorsValues: ReadSensorsValues,
    val writeOutputs: WriteOutputs,
    val startScanForPeripherals: StartScanForPeripherals,
    val stopScanForPeripherals: StopScanForPeripherals,
    val requestMtuFromPeripheral: RequestMtuFromPeripheral,
    val setConnectionStatusObserver: SetConnectionStatusObserver,
    val autoConnectPeripheral: AutoConnectPeripheral,
    val readControllerConfig: ReadControllerConfig
)