package com.dikoresearchsuspensioncontroller.feature_controller.domain.model.controller_models

data class ControllerConfig(
    val version: String = "0.0",
    val isCalibrated: Boolean = false,
    val autoMode: Boolean = false
)