package com.dikoresearchsuspensioncontroller.feature_controller.domain.usecases.suspensioncontroller

import arrow.core.Either
import com.dikoresearchsuspensioncontroller.feature_controller.domain.model.controller_models.OutputsValue
import com.dikoresearchsuspensioncontroller.feature_controller.domain.model.error.BleError
import com.dikoresearchsuspensioncontroller.feature_controller.domain.repository.ble.SuspensionControllerRepository

class WriteOutputs(
    private val suspensionController: SuspensionControllerRepository
) {
    suspend operator fun invoke(outputsValue: OutputsValue): Either<BleError, Unit> {
        return suspensionController.writeOutputs(outputsValue)
    }
}