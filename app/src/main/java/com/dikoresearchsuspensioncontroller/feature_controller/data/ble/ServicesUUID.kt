package com.dikoresearchsuspensioncontroller.feature_controller.data.ble

import java.util.*

object ServicesUUID {
    val CONTROL_SERVICE_UUID = UUID.fromString("d2309610-80dd-11ec-a8a3-0242ac120002")
    val OUTPUTS_CHAR_UUID = UUID.fromString("d2309611-80dd-11ec-a8a3-0242ac120002")
    val SENSORS_CHAR_UUID = UUID.fromString("d2309612-80dd-11ec-a8a3-0242ac120002")
    val CONFIG_CHAR_UUID = UUID.fromString("d2309613-80dd-11ec-a8a3-0242ac120002")
    val NOTIFICATION_ALARM_CHAR_UUID = UUID.fromString("d2309614-80dd-11ec-a8a3-0242ac120002")
    val PRESSURE_REGULATION_CHAR_UUID = UUID.fromString("d2309615-80dd-11ec-a8a3-0242ac120002")
}