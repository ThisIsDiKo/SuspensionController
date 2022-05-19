package com.dikoresearchsuspensioncontroller.feature_controller.domain.usecases.suspensioncontroller

import android.bluetooth.le.ScanResult
import com.dikoresearchsuspensioncontroller.feature_controller.domain.repository.ble.SuspensionControllerRepository
import com.welie.blessed.BluetoothPeripheral
import com.welie.blessed.ScanFailure

class StartScanForPeripherals(
    private val suspensionController: SuspensionControllerRepository
) {
     operator fun invoke(
        resultCallback: (BluetoothPeripheral, ScanResult) -> Unit,
        scanError: (ScanFailure) -> Unit
    ){
        suspensionController.startScanning(
            resultCallback = resultCallback,
            scanError = scanError
        )
    }
}