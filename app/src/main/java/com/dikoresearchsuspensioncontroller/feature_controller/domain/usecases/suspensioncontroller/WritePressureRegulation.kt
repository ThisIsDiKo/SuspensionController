package com.dikoresearchsuspensioncontroller.feature_controller.domain.usecases.suspensioncontroller

import arrow.core.Either
import com.dikoresearchsuspensioncontroller.feature_controller.domain.model.controller_models.PressureRegulationParameters
import com.dikoresearchsuspensioncontroller.feature_controller.domain.model.error.BleError
import com.dikoresearchsuspensioncontroller.feature_controller.domain.repository.ble.SuspensionControllerRepository

class WritePressureRegulation(
    private val suspensionController: SuspensionControllerRepository
) {
    suspend operator fun invoke(pressureRegulationParameters: PressureRegulationParameters): Either<BleError, Unit> {
        return suspensionController.writePressureRegulationCommand(pressureRegulationParameters)
    }
}