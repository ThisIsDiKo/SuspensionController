package com.dikoresearchsuspensioncontroller.feature_controller.presentation.controlscreen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dikoresearchsuspensioncontroller.feature_controller.domain.model.ApplicationSettings
import com.dikoresearchsuspensioncontroller.feature_controller.domain.model.DeviceMode
import com.dikoresearchsuspensioncontroller.feature_controller.domain.model.PressureSensor
import com.dikoresearchsuspensioncontroller.feature_controller.domain.model.PressureUnits
import com.dikoresearchsuspensioncontroller.feature_controller.domain.model.controller_models.OutputState
import com.dikoresearchsuspensioncontroller.feature_controller.domain.model.controller_models.OutputsValue
import com.dikoresearchsuspensioncontroller.feature_controller.domain.model.controller_models.SensorsRawValues
import com.dikoresearchsuspensioncontroller.feature_controller.domain.model.controller_models.SensorsValues
import com.dikoresearchsuspensioncontroller.feature_controller.domain.repository.local.DataStoreRepository
import com.dikoresearchsuspensioncontroller.feature_controller.domain.usecases.suspensioncontroller.SuspensionControllerUseCases
import com.welie.blessed.ConnectionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ControlScreenViewModel @Inject constructor(
    private val suspensionControllerUseCases: SuspensionControllerUseCases,
    private val dataStoreManager: DataStoreRepository
): ViewModel() {

    private var readingJob: Job? = null

    private val applicationSettingsFlow = dataStoreManager.getApplicationSettingsFlow()

    private val _eventFlow = MutableSharedFlow<UiEventControlScreen>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _showReconnectionDialog = mutableStateOf(false)
    val showReconnectionDialog: State<Boolean> = _showReconnectionDialog

    private val _sensorsDataState = mutableStateOf(SensorsDataState())
    val sensorsDataState: State<SensorsDataState> = _sensorsDataState

    private val _showPressureInTank = mutableStateOf(false)
    val showPressureInTank: State<Boolean> = _showPressureInTank

    private val _deviceMode = mutableStateOf(DeviceMode.DoubleWay().alias)
    val deviceMode: State<String> = _deviceMode

    private var selectedPressureSensor: PressureSensor = PressureSensor.China_0_20()
    private var selectedPressureUnits: PressureUnits = PressureUnits.Bar()

    private var deviceMacAddress = ""

    init {
//        suspensionControllerUseCases.setConnectionStatusObserver {peripheral, state ->
//            Timber.e("Peripheral ${peripheral.address} change state to $state")
//            if (state == ConnectionState.DISCONNECTED && peripheral.address == deviceMacAddress){
//                changeReconnectionDialogState(true)
//                stopReadingSensorsValues()
//                suspensionControllerUseCases.autoConnectPeripheral(peripheral)
//                Timber.e("Trying to reconnect ${peripheral.address}")
//            }
//            else if (state == ConnectionState.CONNECTED){
//                Timber.e("${peripheral.address} reconnected mtu is ${peripheral.currentMtu}")
//                changeReconnectionDialogState(false)
//                if (readingJob == null){
//                    startReadingSensorsValues()
//                }
//
//            }
//        }

        viewModelScope.launch {
            applicationSettingsFlow.collectLatest { settings ->
                deviceMacAddress = settings.deviceAddress
                selectedPressureSensor = settings.pressureSensorType
                selectedPressureUnits = settings.pressureUnits
                _showPressureInTank.value = settings.useTankPressure
                _deviceMode.value = settings.deviceMode.alias
            }
        }
    }
    fun changeReconnectionDialogState(state: Boolean){
        _showReconnectionDialog.value = state
    }

    fun setConnectionStateObserver(){
        suspensionControllerUseCases.setConnectionStatusObserver {peripheral, state ->
            Timber.e("Peripheral ${peripheral.address} change state to $state")
            if (state == ConnectionState.DISCONNECTED && peripheral.address == deviceMacAddress){
                changeReconnectionDialogState(true)
                stopReadingSensorsValues()
                suspensionControllerUseCases.autoConnectPeripheral(peripheral)
                Timber.e("Trying to reconnect ${peripheral.address}")
            }
            else if (state == ConnectionState.CONNECTED){
                Timber.e("${peripheral.address} reconnected mtu is ${peripheral.currentMtu}")
                changeReconnectionDialogState(false)
                if (readingJob == null){
                    startReadingSensorsValues()
                }

            }
        }
    }

    fun clearConnectionStateObserver(){
        suspensionControllerUseCases.setConnectionStatusObserver {peripheral, state ->
        }
    }

    fun writeOutputs(msg: String){
        val outputsValue = OutputsValue(10)
        when(msg){
            "0001" -> outputsValue.setOutput(0, OutputState.HIGH)
            "0010" -> outputsValue.setOutput(1, OutputState.HIGH)
            "0100" -> outputsValue.setOutput(2, OutputState.HIGH)
            "1000" -> outputsValue.setOutput(3, OutputState.HIGH)
            "0101" -> {
                outputsValue.setOutput(0, OutputState.HIGH)
                outputsValue.setOutput(2, OutputState.HIGH)
            }
            "1010" -> {
                outputsValue.setOutput(1, OutputState.HIGH)
                outputsValue.setOutput(3, OutputState.HIGH)
            }
            else -> {}
        }
        viewModelScope.launch {
            suspensionControllerUseCases.writeOutputs(outputsValue)
                .fold(
                    {
                        Timber.e("Error while writing info")
                        viewModelScope.launch {
                            _eventFlow.emit(
                                UiEventControlScreen.ShowSnackbar("Error while writing outputs")
                            )
                        }
                    },
                    {

                    }
                )
        }
    }

    fun startReadingSensorsValues(){
        if (readingJob != null) return
        //TODO: need to get connection status to start this job

        readingJob = viewModelScope.launch {
            while(true){
                suspensionControllerUseCases.readSensorsValues()
                    .fold(
                        {
                            Timber.e("Error while reading sensors")
                            viewModelScope.launch {
                                _eventFlow.emit(
                                    UiEventControlScreen.ShowSnackbar("Error while reading sensors")
                                )
                            }
                        },
                        {rawValues ->
                            val sensorsValues = SensorsValues()
                            //Timber.i("Sensors raw data: $rawValues")
                            when(selectedPressureSensor){
                                is PressureSensor.China_0_20 -> {
                                    sensorsValues.calculateFromChinaSensor(rawValues)
                                }
                                is PressureSensor.Catterpillar -> {
                                    sensorsValues.calculateFromCaterpillarSensor(rawValues)
                                }
                            }

                            when(selectedPressureUnits){
                                is PressureUnits.Bar -> {
                                    //Timber.i("Sensors data: $sensorsValues")
                                    _sensorsDataState.value = sensorsDataState.value.copy(
                                        pressure1 = if(sensorsValues.pressure1 >= 0.0)  String.format("%.1f", sensorsValues.pressure1) else "----",
                                        pressure2 = if(sensorsValues.pressure2 >= 0.0)  String.format("%.1f", sensorsValues.pressure2) else "----",
                                        pressureTank = if(sensorsValues.pressureTank >= 0.0)  String.format("%.1f", sensorsValues.pressureTank) else "----"
                                    )
                                }
                                is PressureUnits.Psi -> {
                                    sensorsValues.convertToPsi()
                                    //Timber.i("Sensors data: $sensorsValues")
                                    _sensorsDataState.value = sensorsDataState.value.copy(
                                        pressure1 = if(sensorsValues.pressure1 >= 0.0)  String.format("%.0f", sensorsValues.pressure1) else "----",
                                        pressure2 = if(sensorsValues.pressure2 >= 0.0)  String.format("%.0f", sensorsValues.pressure2) else "----",
                                        pressureTank = if(sensorsValues.pressureTank >= 0.0)  String.format("%.0f", sensorsValues.pressureTank) else "----"
                                    )
                                }
                            }
                        }
                    )
                delay(500)
            }

        }
    }

    fun stopReadingSensorsValues(){
        if (readingJob != null){
            println("cancelling Job")
            readingJob?.cancel()
            readingJob = null
        }
    }
}