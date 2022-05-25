package com.dikoresearchsuspensioncontroller.feature_controller.presentation.scanscreen

data class BleSimpleDevice(
    val MAC: String = "",
    val name: String = "",
    var rssi: String = ""
)
