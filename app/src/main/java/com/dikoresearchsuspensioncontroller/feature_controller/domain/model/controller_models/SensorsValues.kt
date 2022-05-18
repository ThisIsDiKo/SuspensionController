package com.dikoresearchsuspensioncontroller.feature_controller.domain.model.controller_models

data class SensorsValues(
    val pressure1: Double = 0.0,
    val pressure2: Double = 0.0,
    val pressure3: Double = 0.0,
    val pressure4: Double = 0.0,
    val pressureTank: Double = 0.0,

    val pos1: Int = 0,
    val pos2: Int = 0,
    val pos3: Int = 0,
    val pos4: Int = 0,

    val error: String = ""
)