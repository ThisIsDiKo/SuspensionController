package com.dikoresearchsuspensioncontroller.feature_controller.domain.repository.ble

import android.bluetooth.le.ScanResult
import arrow.core.Either
import com.dikoresearchsuspensioncontroller.feature_controller.domain.model.controller_models.ControllerConfig
import com.dikoresearchsuspensioncontroller.feature_controller.domain.model.controller_models.OutputsValue
import com.dikoresearchsuspensioncontroller.feature_controller.domain.model.controller_models.PressureRegulationParameters
import com.dikoresearchsuspensioncontroller.feature_controller.domain.model.controller_models.SensorsRawValues
import com.dikoresearchsuspensioncontroller.feature_controller.domain.model.error.BleError
import com.welie.blessed.BluetoothPeripheral
import com.welie.blessed.ConnectionState
import com.welie.blessed.ScanFailure

interface SuspensionControllerRepository {
    fun createPeripheral(mac: String): Either<BleError, BluetoothPeripheral>
    fun getCurrentPeripheral(): BluetoothPeripheral?
    fun observeConnectionStatus(connectionCallback: (peripheral: BluetoothPeripheral, state: ConnectionState) -> Unit)
    fun startScanning(resultCallback: (BluetoothPeripheral, ScanResult) -> Unit, scanError: (ScanFailure) -> Unit)
    fun stopScanning()

    suspend fun requestMtu(mtu: Int): Either<BleError, Unit>

    suspend fun connectToPeripheral(peripheral: BluetoothPeripheral): Either<BleError, Unit>
    suspend fun disconnectFromPeripheral(peripheral: BluetoothPeripheral): Either<BleError, Unit>
    suspend fun writeOutputs(outputs: OutputsValue): Either<BleError, Unit>
    suspend fun readSensors(): Either<BleError, SensorsRawValues>
    suspend fun writeConfig(): Either<BleError, Unit>
    suspend fun readConfig(): Either<BleError, ControllerConfig>
    suspend fun writeCalibrationCommand(): Either<BleError, Unit>
    suspend fun writePressureRegulationCommand(regulationParameters: PressureRegulationParameters): Either<BleError, Unit>

    suspend fun observeNotifications(notificationCallback: (ByteArray) -> Unit)
    suspend fun stopObserveNotifications()

    fun autoConnectToPeripheral(peripheral: BluetoothPeripheral)
    fun getConnectionState(): ConnectionState
}