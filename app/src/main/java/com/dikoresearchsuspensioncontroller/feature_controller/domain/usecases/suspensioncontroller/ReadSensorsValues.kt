package com.dikoresearchsuspensioncontroller.feature_controller.domain.usecases.suspensioncontroller

import arrow.core.Either
import com.dikoresearchsuspensioncontroller.feature_controller.domain.model.controller_models.SensorsRawValues
import com.dikoresearchsuspensioncontroller.feature_controller.domain.model.error.BleError
import com.dikoresearchsuspensioncontroller.feature_controller.domain.repository.ble.SuspensionControllerRepository

class ReadSensorsValues(
    private val suspensionController: SuspensionControllerRepository
) {
    suspend operator fun invoke(): Either<BleError, SensorsRawValues> {
        return suspensionController.readSensors()
    }
}