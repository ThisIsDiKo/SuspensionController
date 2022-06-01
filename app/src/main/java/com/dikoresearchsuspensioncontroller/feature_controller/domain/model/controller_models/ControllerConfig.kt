package com.dikoresearchsuspensioncontroller.feature_controller.domain.model.controller_models

data class ControllerConfig(
    val version: String = "0.0.0",
    val hasTank: Boolean = false,
    val numberOfCounters: Int = 1
)