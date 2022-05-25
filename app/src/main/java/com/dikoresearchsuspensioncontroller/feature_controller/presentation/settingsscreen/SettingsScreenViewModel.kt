package com.dikoresearchsuspensioncontroller.feature_controller.presentation.settingsscreen

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dikoresearchsuspensioncontroller.feature_controller.domain.model.*
import com.dikoresearchsuspensioncontroller.feature_controller.domain.repository.local.DataStoreRepository
import com.dikoresearchsuspensioncontroller.feature_controller.domain.usecases.suspensioncontroller.SuspensionControllerUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val suspensionControllerUseCases: SuspensionControllerUseCases,
    private val dataStoreManager: DataStoreRepository
): ViewModel() {

    val applicationSettingsFlow = dataStoreManager.getApplicationSettingsFlow()

    private val _eventFlow = MutableSharedFlow<UiEventSettingsScreen>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun setUseTankPressure(use: Boolean){
        viewModelScope.launch {
            dataStoreManager.setUseTankPressure(use)
        }
    }
    fun setDeviceMode(mode: DeviceMode){
        viewModelScope.launch {
            dataStoreManager.setDeviceMode(mode)
        }
    }

    fun setPressureSensor(pressureSensor: PressureSensor){
        viewModelScope.launch {
            dataStoreManager.setPressureSensor(pressureSensor)
        }
    }

    fun setPressureUnits(pressureUnits: PressureUnits){
        viewModelScope.launch {
            dataStoreManager.setPressureUnits(pressureUnits)
        }
    }

    fun navigateUp(){
        viewModelScope.launch {
            _eventFlow.emit(
                UiEventSettingsScreen.NavigateUp
            )
        }
    }

    fun clearDeviceInfo(deviceInfo: ApplicationSettings){
        viewModelScope.launch {
            suspensionControllerUseCases.disconnectFromPeripheral()
            dataStoreManager.setDeviceAddress("")
            dataStoreManager.setDeviceName("")
            dataStoreManager.setDeviceType(DeviceType.SimplePressure())
            dataStoreManager.setDeviceMode(DeviceMode.DoubleWay())
            dataStoreManager.setDeviceFirmwareVersion("")
            dataStoreManager.setUseTankPressure(false)
            dataStoreManager.setPressureSensor(PressureSensor.China_0_20())
            dataStoreManager.setPressureUnits(PressureUnits.Bar())

            _eventFlow.emit(
                UiEventSettingsScreen.NavigateTo("scanscreen")
            )
        }
    }
}