package com.dikoresearchsuspensioncontroller.feature_controller.domain.model.error

sealed class BleError(val error: String){
    class InvalidSensorsDataPacket(error: String): BleError(error)
    class InvalidMacAddress(error: String): BleError(error)
    class PeripheralIsNull(error: String): BleError(error)
    class UnknownError(error: String): BleError(error)
}
