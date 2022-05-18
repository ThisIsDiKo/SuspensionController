package com.dikoresearchsuspensioncontroller.feature_controller.data.ble

import android.bluetooth.le.ScanResult
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.dikoresearchsuspensioncontroller.feature_controller.domain.model.controller_models.ControllerConfig
import com.dikoresearchsuspensioncontroller.feature_controller.domain.model.controller_models.OutputsValue
import com.dikoresearchsuspensioncontroller.feature_controller.domain.model.controller_models.SensorsRawValues
import com.dikoresearchsuspensioncontroller.feature_controller.domain.model.error.BleError
import com.dikoresearchsuspensioncontroller.feature_controller.domain.repository.ble.SuspensionControllerRepository
import com.welie.blessed.*

class SuspensionControllerRepositoryImpl(
    private val bleManager: BleManager
): SuspensionControllerRepository {
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
        TODO("Not yet implemented")
    }

    override suspend fun writeCalibrationCommand(): Either<BleError, Unit> {
        TODO("Not yet implemented")
    }

    @Throws(InvalidReceivedDataException::class)
    suspend fun readRawSensorsValues(): SensorsRawValues{
        val byteArray = bleManager.blePeripheral?.readCharacteristic(
            serviceUUID = ServicesUUID.CONTROL_SERVICE_UUID,
            characteristicUUID = ServicesUUID.SENSORS_CHAR_UUID
        )?: ByteArray(1)

        if (byteArray.size == BleCommunicationParameters.sensorsPacketLength){
            val p1 = (byteArray[1].toInt() shl 8) + byteArray[0].toInt()
            val p2 = (byteArray[3].toInt() shl 8) + byteArray[2].toInt()
            val p3 = (byteArray[5].toInt() shl 8) + byteArray[4].toInt()
            val p4 = (byteArray[7].toInt() shl 8) + byteArray[6].toInt()
            val p5 = (byteArray[9].toInt() shl 8) + byteArray[8].toInt()

            val pos1 = byteArray[10].toInt()
            val pos2 = byteArray[11].toInt()
            val pos3 = byteArray[12].toInt()
            val pos4 = byteArray[13].toInt()

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
            )
        }
        else {
            throw InvalidReceivedDataException("Read Sensors got ${byteArray.size} bytes, need ${BleCommunicationParameters.sensorsPacketLength}")
        }
    }
}

class InvalidReceivedDataException(message: String): Exception(message)
class BlePeripheralIsNullException(message: String): Exception(message)