package com.dikoresearchsuspensioncontroller.feature_controller.presentation.scanscreen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dikoresearchsuspensioncontroller.feature_controller.domain.model.*
import com.dikoresearchsuspensioncontroller.feature_controller.domain.repository.local.DataStoreRepository
import com.dikoresearchsuspensioncontroller.feature_controller.domain.usecases.suspensioncontroller.SuspensionControllerUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ScanScreenViewModel @Inject constructor(
    private val suspensionControllerUseCases: SuspensionControllerUseCases,
    private val dataStoreManager: DataStoreRepository
): ViewModel(){
    private val applicationSettingsFlow = dataStoreManager.getApplicationSettingsFlow()

    private val _bleDevices = mutableStateListOf<BleSimpleDevice>()
    val bleDevices: SnapshotStateList<BleSimpleDevice> = _bleDevices

    private val _showConnectionDialog = mutableStateOf(false)
    val showConnectionDialog: State<Boolean> = _showConnectionDialog

    private val _refreshScanResults = mutableStateOf(false)
    val refreshScanResults: State<Boolean> = _refreshScanResults

    private val _eventFlow = MutableSharedFlow<UiEventScanScreen>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun startScanning(){
        _bleDevices.clear()
        suspensionControllerUseCases.startScanForPeripherals(
            resultCallback = { peripheral, scanResult ->
                if (peripheral.name.startsWith("")){
                    var index = -1
                    for (i in _bleDevices.indices){
                        if (_bleDevices[i].MAC == peripheral.address){
                            index = i
                        }
                    }
                    if (index != -1){
                        _bleDevices[index] = BleSimpleDevice(
                            MAC = peripheral.address,
                            name = peripheral.name.ifBlank { "Unknown" },
                            rssi = scanResult.rssi.toString()
                        )
                    }
                    else {
                        _bleDevices.add(BleSimpleDevice(
                            MAC = peripheral.address,
                            name = peripheral.name.ifBlank { "Unknown" },
                            rssi = scanResult.rssi.toString()
                        ))
                    }
                }
            },
            scanError = {scanFailure ->
                viewModelScope.launch { _eventFlow.emit(
                    UiEventScanScreen.ShowSnackbar("Scan failed ${scanFailure.toString()}")
                )
                }
            }
        )
    }
    fun stopScanning(){
        suspensionControllerUseCases.stopScanForPeripherals()
    }

    fun refreshScanning(){
        _refreshScanResults.value = true
        _bleDevices.clear()
        _refreshScanResults.value = false
    }

    fun deviceSelect(device: BleSimpleDevice){
        stopScanning()
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                _showConnectionDialog.value = true
                suspensionControllerUseCases.connectToPeripheral(device.MAC)
                    .fold(
                        {
                            _showConnectionDialog.value = false
                            _eventFlow.emit(
                                UiEventScanScreen.ShowSnackbar("can't connect to ${device.MAC}")
                            )
                            startScanning()
                        },
                        {
                            //Сохраняем информацию о подключенном устройстве и переходим на экран управления
                            dataStoreManager.setDeviceAddress(device.MAC)
                            dataStoreManager.setDeviceName(device.name)
                            dataStoreManager.setDeviceType(DeviceType.SimplePressure())
                            dataStoreManager.setDeviceMode(DeviceMode.DoubleWay())
                            dataStoreManager.setDeviceFirmwareVersion("0.0.1")
                            dataStoreManager.setUseTankPressure(false)
                            dataStoreManager.setPressureSensor(PressureSensor.China_0_20())
                            dataStoreManager.setPressureUnits(PressureUnits.Bar())

                            _showConnectionDialog.value = false

                            _eventFlow.emit(
                                UiEventScanScreen.NavigateTo("controlscreen")
                            )
                        }
                    )
            }
        }
    }
}