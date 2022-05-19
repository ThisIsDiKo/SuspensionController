package com.dikoresearchsuspensioncontroller.feature_controller.domain.usecases.suspensioncontroller

import arrow.core.Either
import arrow.core.left
import com.dikoresearchsuspensioncontroller.feature_controller.domain.model.error.BleError
import com.dikoresearchsuspensioncontroller.feature_controller.domain.repository.ble.SuspensionControllerRepository

class ConnectToPeripheral(
    private val suspensionController: SuspensionControllerRepository
) {
    suspend operator fun invoke(mac: String): Either<BleError, Unit>{
        val peripheral = suspensionController.createPeripheral(mac)
            .fold(
                { error ->
                    return error.left()
                },
                { peripheral ->
                    peripheral
                }
            )
        return suspensionController.connectToPeripheral(peripheral)
    }
}