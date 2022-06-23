package com.dikoresearchsuspensioncontroller.feature_controller.domain.usecases.suspensioncontroller

import com.dikoresearchsuspensioncontroller.feature_controller.domain.repository.ble.SuspensionControllerRepository

class StopObserveNotification(
    private val suspensionController: SuspensionControllerRepository
) {
    suspend operator fun invoke() {
        suspensionController.stopObserveNotifications()
    }
}