package com.dikoresearchsuspensioncontroller.feature_controller.presentation.settingsscreen

import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dikoresearchsuspensioncontroller.feature_controller.domain.model.*
import com.dikoresearchsuspensioncontroller.feature_controller.domain.repository.local.DataStoreRepository
import com.dikoresearchsuspensioncontroller.feature_controller.domain.usecases.suspensioncontroller.SuspensionControllerUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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

    private val _showClearDialog = mutableStateOf(false)
    val showClearDialog: State<Boolean> = _showClearDialog

    fun setUseTankPressure(use: Boolean){
        viewModelScope.launch {
            dataStoreManager.setUseTankPressure(use)
            clearPressurePresets()
        }
    }
    fun setDeviceMode(mode: DeviceMode){
        viewModelScope.launch {
            dataStoreManager.setDeviceMode(mode)
            clearPressurePresets()
        }
    }

    fun setAirPreparingSystem(airPreparingSystem: AirPreparingSystem){
        viewModelScope.launch {
            dataStoreManager.setAirPreparingSystem(airPreparingSystem)
            clearPressurePresets()
        }
    }

    fun setPressureSensor(pressureSensor: PressureSensor){
        viewModelScope.launch {
            dataStoreManager.setPressureSensor(pressureSensor)
        }
    }

    fun setShowControlGroup(use: Boolean){
        viewModelScope.launch {
            dataStoreManager.setShowControlGroup(use)
            if (!use){
                dataStoreManager.setShowRegulationGroup(use)
            }
            clearPressurePresets()
        }
    }

    fun setShowRegulationGroup(use: Boolean){
        viewModelScope.launch {
            dataStoreManager.setShowRegulationGroup(use)
            clearPressurePresets()
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

    fun disconnect(){
        viewModelScope.launch(Dispatchers.IO) {
            suspensionControllerUseCases.disconnectFromPeripheral()
        }
    }

    fun clearDeviceInfo(deviceInfo: ApplicationSettings){
        viewModelScope.launch {
            suspensionControllerUseCases.setConnectionStatusObserver{peripheral, state ->
            }

            suspensionControllerUseCases.disconnectFromPeripheral()
            dataStoreManager.setDeviceAddress("")
            dataStoreManager.setDeviceName("")
            dataStoreManager.setDeviceType(DeviceType.SimplePressure())
            dataStoreManager.setDeviceMode(DeviceMode.DoubleWay())
            dataStoreManager.setDeviceFirmwareVersion("")
            dataStoreManager.setUseTankPressure(false)
            dataStoreManager.setPressureSensor(PressureSensor.China_0_20())
            dataStoreManager.setPressureUnits(PressureUnits.Bar())

            clearPressurePresets()

            _eventFlow.emit(
                UiEventSettingsScreen.NavigateTo("scanscreen")
            )
        }
    }

    fun showClearDialog(){
        _showClearDialog.value = true
    }

    fun dialogDismissRequest(){
        _showClearDialog.value = false
    }

    suspend fun clearPressurePresets(){
        dataStoreManager.setPressurePreset(1, "0,0,0,0")
        dataStoreManager.setPressurePreset(2, "0,0,0,0")
        dataStoreManager.setPressurePreset(3, "0,0,0,0")
    }
}