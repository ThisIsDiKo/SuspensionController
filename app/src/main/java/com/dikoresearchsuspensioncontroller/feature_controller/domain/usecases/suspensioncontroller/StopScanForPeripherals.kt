package com.dikoresearchsuspensioncontroller.feature_controller.domain.usecases.suspensioncontroller

import com.dikoresearchsuspensioncontroller.feature_controller.domain.repository.ble.SuspensionControllerRepository

class StopScanForPeripherals(
    private val suspensionController: SuspensionControllerRepository
) {
    operator fun invoke(){
        suspensionController.stopScanning()
    }
}