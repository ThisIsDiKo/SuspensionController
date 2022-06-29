package com.dikoresearchsuspensioncontroller.feature_controller.data.ble

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.le.ScanResult
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.dikoresearchsuspensioncontroller.feature_controller.data.ble.ServicesUUID.CONTROL_SERVICE_UUID
import com.dikoresearchsuspensioncontroller.feature_controller.data.ble.ServicesUUID.NOTIFICATION_ALARM_CHAR_UUID
import com.dikoresearchsuspensioncontroller.feature_controller.domain.model.controller_models.ControllerConfig
import com.dikoresearchsuspensioncontroller.feature_controller.domain.model.controller_models.OutputsValue
import com.dikoresearchsuspensioncontroller.feature_controller.domain.model.controller_models.PressureRegulationParameters
import com.dikoresearchsuspensioncontroller.feature_controller.domain.model.controller_models.SensorsRawValues
import com.dikoresearchsuspensioncontroller.feature_controller.domain.model.error.BleError
import com.dikoresearchsuspensioncontroller.feature_controller.domain.repository.ble.SuspensionControllerRepository
import com.welie.blessed.*
import timber.log.Timber

class SuspensionControllerRepositoryImpl(
    private val bleManager: BleManager
): SuspensionControllerRepository {

    var notificationChar: BluetoothGattCharacteristic? = null

    override fun getConnectionState(): ConnectionState{
        return bleManager.blePeripheral?.getState() ?: ConnectionState.DISCONNECTED
    }

    override fun createPeripheral(mac: String): Either<BleError, BluetoothPeripheral> {
        return try {
            bleManager.bleCentralManager.getPeripheral(mac).right()
        }
        catch (e: Exception){
            BleError.UnknownError(e.toString()).left()
        }
    }

    override fun getCurrentPeripheral(): BluetoothPeripheral? {
        return bleManager.blePeripheral
    }

    override fun observeConnectionStatus(connectionCallback: (peripheral: BluetoothPeripheral, state: ConnectionState) -> Unit) {
        bleManager.bleCentralManager.observeConnectionState { peripheral, state ->
            connectionCallback(peripheral, state)
        }
    }

    override fun startScanning(
        resultCallback: (BluetoothPeripheral, ScanResult) -> Unit,
        scanError: (ScanFailure) -> Unit
    ) {
        bleManager.bleCentralManager.setScanMode(ScanMode.BALANCED)
        bleManager.bleCentralManager.scanForPeripherals(
            resultCallback = resultCallback,
            scanError = scanError
        )
    }

    override fun stopScanning() {
        bleManager.bleCentralManager.stopScan()
    }

    override suspend fun requestMtu(mtu: Int): Either<BleError, Unit> {
        return try {
            if (bleManager.blePeripheral?.requestMtu(mtu) == mtu){
                Unit.right()
            }
            else {
                BleError.UnknownError("Can't get mtu").left()
            }
        }
        catch (e: Exception){
            BleError.UnknownError(e.toString()).left()
        }
    }

    override suspend fun connectToPeripheral(peripheral: BluetoothPeripheral): Either<BleError, Unit> {
        return try {
            bleManager.bleCentralManager.connectPeripheral(peripheral)
            bleManager.blePeripheral = peripheral
            Unit.right()
        } catch (e: Exception){
            BleError.UnknownError(e.toString()).left()
        }
    }

    override suspend fun disconnectFromPeripheral(peripheral: BluetoothPeripheral): Either<BleError, Unit> {
        return try {
            bleManager.bleCentralManager.cancelConnection(peripheral)
            bleManager.blePeripheral = null
            Unit.right()
        }
        catch (e: Exception){
            BleError.UnknownError(e.toString()).left()
        }
    }

    override suspend fun writeOutputs(outputs: OutputsValue): Either<BleError, Unit> {
        return try {
            bleManager.blePeripheral?.writeCharacteristic(
                serviceUUID = ServicesUUID.CONTROL_SERVICE_UUID,
                characteristicUUID = ServicesUUID.OUTPUTS_CHAR_UUID,
                value = outputs.convertOutputsToByteArray(),
                writeType = WriteType.WITH_RESPONSE
            ) ?: BleError.PeripheralIsNull("Peripheral is null").left()
            Unit.right()
        }
        catch (e: Exception){
            BleError.UnknownError(e.toString() ?: "").left()
        }
    }

    override suspend fun readSensors(): Either<BleError, SensorsRawValues> {
        return try {
            val rawSensorsValues = readRawSensorsValues()
            rawSensorsValues.right()
        }
        catch (e: Exception){
            when (e){
                is InvalidReceivedDataException -> {
                    BleError.InvalidSensorsDataPacket(e.message ?: "").left()
                }
                else -> {
                    BleError.UnknownError(e.message ?: "").left()
                }
            }
        }
    }

    override suspend fun writeConfig(): Either<BleError, Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun readConfig(): Either<BleError, ControllerConfig> {
        return try{
            readRawConfig().right()
        }
        catch (e: Exception){
            BleError.UnknownError(e.message ?: "").left()
        }
    }

    override suspend fun writeCalibrationCommand(): Either<BleError, Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun writePressureRegulationCommand(regulationParameters: PressureRegulationParameters): Either<BleError, Unit> {
        val commandType: Byte = if (regulationParameters.commandType == "START") 1.toByte() else 0.toByte()
        val waysType: Byte = if (regulationParameters.waysType == "SINGLE") 2.toByte() else 1.toByte()
        val airPreparingType: Byte = if (regulationParameters.airPreparingType == "RECEIVER") 1.toByte() else 0.toByte()
        val useTank: Byte = if (regulationParameters.useTankPressure) 1.toByte() else 0.toByte()

        val pressure1 = regulationParameters.refPressure1mV
        val pressure2 = regulationParameters.refPressure2mV
        val pressure3 = regulationParameters.refPressure3mV
        val pressure4 = regulationParameters.refPressure4mV

        val bytesToWrite: ByteArray = byteArrayOf(
            commandType,
            waysType,
            airPreparingType,
            useTank,
            pressure1.toByte(),
            (pressure1 shr 8).toByte(),
            pressure2.toByte(),
            (pressure2 shr 8).toByte(),
            pressure3.toByte(),
            (pressure3 shr 8).toByte(),
            pressure4.toByte(),
            (pressure4 shr 8).toByte(),
        )
        Timber.i("Calculated new regulationParameters info")
        for (b in bytesToWrite){
            Timber.i("${b.toUByte()}")
        }

        return try {
            bleManager.blePeripheral?.writeCharacteristic(
                serviceUUID = ServicesUUID.CONTROL_SERVICE_UUID,
                characteristicUUID = ServicesUUID.PRESSURE_REGULATION_CHAR_UUID,
                value = bytesToWrite,
                writeType = WriteType.WITH_RESPONSE
            ) ?: BleError.PeripheralIsNull("Peripheral is null").left()
            Timber.i("Data transfered")
            Unit.right()
        }
        catch (e: Exception){
            Timber.e("Data transfer error: $e")
            BleError.UnknownError(e.toString() ?: "").left()
        }
    }

    override suspend fun observeNotifications(notificationCallback: (ByteArray) -> Unit) {
        notificationChar = bleManager.blePeripheral?.getCharacteristic(
            serviceUUID = CONTROL_SERVICE_UUID,
            characteristicUUID =NOTIFICATION_ALARM_CHAR_UUID
        )
        if (notificationChar != null){
            bleManager.blePeripheral?.observe(notificationChar!!, callback = notificationCallback)
        }
    }

    override suspend fun stopObserveNotifications() {
        if (notificationChar != null){
            if (bleManager.blePeripheral?.getState() == ConnectionState.CONNECTED){
                bleManager.blePeripheral?.stopObserving(notificationChar!!)
                notificationChar = null
            }

        }
    }

    override fun autoConnectToPeripheral(peripheral: BluetoothPeripheral) {
        bleManager.bleCentralManager.autoConnectPeripheral(peripheral)
    }

    @Throws(InvalidReceivedDataException::class)
    suspend fun readRawSensorsValues(): SensorsRawValues{
        val byteArray = bleManager.blePeripheral?.readCharacteristic(
            serviceUUID = ServicesUUID.CONTROL_SERVICE_UUID,
            characteristicUUID = ServicesUUID.SENSORS_CHAR_UUID
        )?: ByteArray(1)

        if (byteArray.size == BleCommunicationParameters.sensorsPacketLength){
            val p1 = (byteArray[1].toInt() shl 8) + byteArray[0].toUByte().toInt()
            val p2 = (byteArray[3].toInt() shl 8) + byteArray[2].toUByte().toInt()
            val p3 = (byteArray[5].toInt() shl 8) + byteArray[4].toUByte().toInt()
            val p4 = (byteArray[7].toInt() shl 8) + byteArray[6].toUByte().toInt()
            val p5 = (byteArray[9].toInt() shl 8) + byteArray[8].toUByte().toInt()

            Timber.i("Read data $p1 (${byteArray[1]} ${byteArray[0]}) $p2 (${byteArray[3]} ${byteArray[2]}) $p5 (${byteArray[9]} ${byteArray[8]})")

            val pos1 = byteArray[10].toInt()
            val pos2 = byteArray[11].toInt()
            val pos3 = byteArray[12].toInt()
            val pos4 = byteArray[13].toInt()

            val flag = byteArray[14].toInt()

            return SensorsRawValues(
                pressure1_mV = p1,
                pressure2_mV = p2,
                pressure3_mV = p3,
                pressure4_mV = p4,
                pressure5_mV = p5,

                pos1 = pos1,
                pos2 = pos2,
                pos3 = pos3,
                pos4 = pos4,

                flag = flag
            )
        }
        else {
            throw InvalidReceivedDataException("Read Sensors got ${byteArray.size} bytes, need ${BleCommunicationParameters.sensorsPacketLength}")
        }
    }

    @Throws(InvalidReceivedDataException::class)
    suspend fun readRawConfig(): ControllerConfig{
        val byteArray = bleManager.blePeripheral?.readCharacteristic(
            serviceUUID = ServicesUUID.CONTROL_SERVICE_UUID,
            characteristicUUID = ServicesUUID.CONFIG_CHAR_UUID
        )?: ByteArray(1)

        if (byteArray.size == BleCommunicationParameters.configPacketLength){
            val majorVersion = byteArray[0].toUByte().toInt()
            val minorVersion = byteArray[1].toUByte().toInt()
            val subVersion = byteArray[2].toUByte().toInt()
            val numberOfCounters = byteArray[3].toUByte().toInt()
            val hasTank = byteArray[4].toUByte().toInt() == 1



            return ControllerConfig(
                version = "$majorVersion.$minorVersion.$subVersion",
                hasTank = hasTank,
                numberOfCounters = numberOfCounters
            )
        }
        else {
            throw InvalidReceivedDataException("Read Config data got ${byteArray.size} bytes, need ${BleCommunicationParameters.sensorsPacketLength}")
        }
    }
}

class InvalidReceivedDataException(message: String): Exception(message)
class BlePeripheralIsNullException(message: String): Exception(message)