package com.dikoresearchsuspensioncontroller.feature_controller.domain.model.controller_models

data class SensorsRawValues(
    val pressure1_mV: Int = 0,
    val pressure2_mV: Int = 0,
    val pressure3_mV: Int = 0,
    val pressure4_mV: Int = 0,
    val pressure5_mV: Int = 0,

    val pos1: Int = 0,
    val pos2: Int = 0,
    val pos3: Int = 0,
    val pos4: Int = 0,

    val error: String = ""
)
