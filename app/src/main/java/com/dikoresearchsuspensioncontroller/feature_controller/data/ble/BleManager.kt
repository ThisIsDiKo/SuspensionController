package com.dikoresearchsuspensioncontroller.feature_controller.data.ble

import android.content.Context
import com.welie.blessed.BluetoothCentralManager
import com.welie.blessed.BluetoothPeripheral

class BleManager(
    context: Context
) {
    val bleCentralManager: BluetoothCentralManager = BluetoothCentralManager(context)
    var blePeripheral: BluetoothPeripheral? = null
}