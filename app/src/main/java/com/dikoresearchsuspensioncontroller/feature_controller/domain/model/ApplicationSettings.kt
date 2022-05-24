package com.dikoresearchsuspensioncontroller.feature_controller.domain.model

data class ApplicationSettings(
    val deviceAddress: String,
    val deviceName: String,
    val deviceType: DeviceType,
    val deviceFirmwareVersion: String,
    //-------------
    val deviceMode: DeviceMode,
    val useTankPressure: Boolean,
    val pressureSensorType: PressureSensor,
    val pressureUnits: PressureUnits,
)

sealed class PressureSensor(val alias: String){
    class China_0_20(): PressureSensor("China")
    class Catterpillar(): PressureSensor("Catterpillar")
}

sealed class PressureUnits(val alias: String){
    class Bar(): PressureUnits("Bar")
    class Psi(): PressureUnits("Psi")
}

sealed class DeviceMode(val alias: String){
    class SingleWay(): DeviceMode("Single")
    class DoubleWay(): DeviceMode("Double")
    class QuadroWay(): DeviceMode("Quadro")
}

sealed class DeviceType(val alias: String){
    class SimplePressure(): DeviceType("Simple only Pressure")
    class QuadroPressure(): DeviceType("Quadro only Pressure")
}



