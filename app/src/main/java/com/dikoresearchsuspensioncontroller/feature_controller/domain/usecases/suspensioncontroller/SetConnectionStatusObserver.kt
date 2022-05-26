package com.dikoresearchsuspensioncontroller.feature_controller.domain.usecases.suspensioncontroller

import com.dikoresearchsuspensioncontroller.feature_controller.domain.repository.ble.SuspensionControllerRepository
import com.welie.blessed.BluetoothPeripheral
import com.welie.blessed.ConnectionState


class SetConnectionStatusObserver(
    private val suspensionController: SuspensionControllerRepository
) {
    operator fun invoke(connectionCallback: (peripheral: BluetoothPeripheral, state: ConnectionState) -> Unit) {
        suspensionController.observeConnectionStatus(connectionCallback)
    }
}
