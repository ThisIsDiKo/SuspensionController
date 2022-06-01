package com.dikoresearchsuspensioncontroller.feature_controller.domain.usecases.suspensioncontroller

import arrow.core.Either
import com.dikoresearchsuspensioncontroller.feature_controller.domain.model.controller_models.ControllerConfig
import com.dikoresearchsuspensioncontroller.feature_controller.domain.model.error.BleError
import com.dikoresearchsuspensioncontroller.feature_controller.domain.repository.ble.SuspensionControllerRepository


class ReadControllerConfig(
    private val suspensionController: SuspensionControllerRepository
) {
    suspend operator fun invoke(): Either<BleError, ControllerConfig> {
        return suspensionController.readConfig()
    }
}