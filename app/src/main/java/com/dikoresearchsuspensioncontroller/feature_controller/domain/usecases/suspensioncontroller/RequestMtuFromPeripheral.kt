package com.dikoresearchsuspensioncontroller.feature_controller.domain.usecases.suspensioncontroller

import arrow.core.Either
import arrow.core.left
import com.dikoresearchsuspensioncontroller.feature_controller.domain.model.error.BleError
import com.dikoresearchsuspensioncontroller.feature_controller.domain.repository.ble.SuspensionControllerRepository

class RequestMtuFromPeripheral(
    private val suspensionController: SuspensionControllerRepository
) {
    suspend operator fun invoke(mtu: Int): Either<BleError, Unit> {
        return suspensionController.requestMtu(mtu)
    }
}