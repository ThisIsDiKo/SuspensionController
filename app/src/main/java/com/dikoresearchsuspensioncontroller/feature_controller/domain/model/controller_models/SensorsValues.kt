package com.dikoresearchsuspensioncontroller.feature_controller.domain.model.controller_models

data class SensorsValues(
    var pressure1: Double = 0.0,
    var pressure2: Double = 0.0,
    var pressure3: Double = 0.0,
    var pressure4: Double = 0.0,
    var pressureTank: Double = 0.0,

    var pos1: Int = 0,
    var pos2: Int = 0,
    var pos3: Int = 0,
    var pos4: Int = 0,

    var error: String = ""
){
    fun calculateFromChinaSensor(rawValues: SensorsRawValues){
        val k = 3.45 / 1000
        val b = -1.725

        pressure1 = if (rawValues.pressure1_mV in 6..4999) k * rawValues.pressure1_mV + b else -1.0
        pressure2 = if (rawValues.pressure2_mV in 6..4999) k * rawValues.pressure2_mV + b else -1.0
        pressure3 = if (rawValues.pressure3_mV in 6..4999) k * rawValues.pressure3_mV + b else -1.0
        pressure4 = if (rawValues.pressure4_mV in 6..4999) k * rawValues.pressure4_mV + b else -1.0
        pressureTank = if (rawValues.pressure5_mV in 6..4999) k * rawValues.pressure5_mV + b else -1.0

    }

    fun calculateFromCaterpillarSensor(rawValues: SensorsRawValues){
        val k = 3.45 / 1000
        val b = -1.725

        pressure1 = if (rawValues.pressure1_mV in 6..4999) k * rawValues.pressure1_mV + b else -1.0
        pressure2 = if (rawValues.pressure2_mV in 6..4999) k * rawValues.pressure2_mV + b else -1.0
        pressure3 = if (rawValues.pressure3_mV in 6..4999) k * rawValues.pressure3_mV + b else -1.0
        pressure4 = if (rawValues.pressure4_mV in 6..4999) k * rawValues.pressure4_mV + b else -1.0
        pressureTank = if (rawValues.pressure5_mV in 6..4999) k * rawValues.pressure5_mV + b else -1.0

    }
    
    fun convertToPsi(){
        pressure1 *= 14.5038
        pressure2 *= 14.5038
        pressure3 *= 14.5038
        pressure4 *= 14.5038
        pressureTank *= 14.5038
    }
}