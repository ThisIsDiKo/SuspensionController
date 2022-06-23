package com.dikoresearchsuspensioncontroller.feature_controller.domain.usecases.suspensioncontroller

import com.dikoresearchsuspensioncontroller.feature_controller.domain.repository.ble.SuspensionControllerRepository
import com.welie.blessed.BluetoothPeripheral
import com.welie.blessed.ConnectionState

class StartObserveNotification(
    private val suspensionController: SuspensionControllerRepository
) {
    suspend operator fun invoke(notificationCallback: (ByteArray) -> Unit) {
        suspensionController.observeNotifications(notificationCallback)
    }
}
