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

    val showControlGroup: Boolean,
    val showRegulationGroup: Boolean,

    val airPreparingSystem: AirPreparingSystem,

    val pressurePreset1: String,
    val pressurePreset2: String,
    val pressurePreset3: String,

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

sealed class AirPreparingSystem(val alias: String){
    class CompressorSystem: AirPreparingSystem("Compressor")
    class ReceiverSystem: AirPreparingSystem("Receiver")
}



