package com.dikoresearchsuspensioncontroller.feature_controller.domain.model.controller_models

data class PressureRegulationParameters(
    val commandType: String,
    val waysType: String,
    val airPreparingType: String,
    val useTankPressure: Boolean,
    val airPressureSensorType: String,
    val refPressure1mV: Int,
    val refPressure2mV: Int,
    val refPressure3mV: Int,
    val refPressure4mV: Int,
)
