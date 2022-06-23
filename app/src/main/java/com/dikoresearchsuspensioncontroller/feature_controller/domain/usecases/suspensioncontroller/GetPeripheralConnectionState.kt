package com.dikoresearchsuspensioncontroller.feature_controller.domain.usecases.suspensioncontroller

import com.dikoresearchsuspensioncontroller.feature_controller.domain.repository.ble.SuspensionControllerRepository
import com.welie.blessed.ConnectionState


class GetPeripheralConnectionState(
    private val suspensionController: SuspensionControllerRepository
) {
    operator fun invoke(): ConnectionState {
        return suspensionController.getConnectionState()
    }
}
