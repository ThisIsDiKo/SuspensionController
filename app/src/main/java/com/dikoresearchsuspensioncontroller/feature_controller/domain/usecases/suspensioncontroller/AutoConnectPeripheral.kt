package com.dikoresearchsuspensioncontroller.feature_controller.domain.usecases.suspensioncontroller

import com.dikoresearchsuspensioncontroller.feature_controller.domain.repository.ble.SuspensionControllerRepository
import com.welie.blessed.BluetoothPeripheral


class AutoConnectPeripheral(
    private val suspensionController: SuspensionControllerRepository
) {
    operator fun invoke(peripheral: BluetoothPeripheral){
        suspensionController.autoConnectToPeripheral(peripheral)
    }
}