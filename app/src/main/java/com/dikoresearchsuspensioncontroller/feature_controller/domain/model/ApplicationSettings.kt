package com.dikoresearchsuspensioncontroller.feature_controller.domain.model

data class ApplicationSettings(
    val deviceAddress: String,
    val deviceName: String,
    val deviceFirmwareVersion: String,
    //-------------
    val deviceMode: String,
    val useTankPressure: Boolean,
    val pressureSensorType: String,
    val pressureUnits: String,
)

enum class PressureSensor{
    CHINA_0_20,
    CAT
}


