package com.dikoresearchsuspensioncontroller.feature_controller.domain.usecases.suspensioncontroller

import arrow.core.Either
import arrow.core.left
import com.dikoresearchsuspensioncontroller.feature_controller.domain.model.error.BleError
import com.dikoresearchsuspensioncontroller.feature_controller.domain.repository.ble.SuspensionControllerRepository

class DisconnectFromPeripheral(
    private val suspensionController: SuspensionControllerRepository
) {
    suspend operator fun invoke(): Either<BleError, Unit> {
        val peripheral = suspensionController.getCurrentPeripheral()

        return if (peripheral != null){
            suspensionController.disconnectFromPeripheral(peripheral)
        } else{
            BleError.UnknownError("Peripheral is Null").left()
        }
    }
}