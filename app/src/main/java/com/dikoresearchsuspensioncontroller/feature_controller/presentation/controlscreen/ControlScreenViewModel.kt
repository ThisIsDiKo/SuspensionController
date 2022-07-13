package com.dikoresearchsuspensioncontroller.feature_controller.presentation.controlscreen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dikoresearchsuspensioncontroller.feature_controller.domain.model.ApplicationSettings
import com.dikoresearchsuspensioncontroller.feature_controller.domain.model.DeviceMode
import com.dikoresearchsuspensioncontroller.feature_controller.domain.model.PressureSensor
import com.dikoresearchsuspensioncontroller.feature_controller.domain.model.PressureUnits
import com.dikoresearchsuspensioncontroller.feature_controller.domain.model.controller_models.*
import com.dikoresearchsuspensioncontroller.feature_controller.domain.repository.local.DataStoreRepository
import com.dikoresearchsuspensioncontroller.feature_controller.domain.usecases.suspensioncontroller.SuspensionControllerUseCases
import com.dikoresearchsuspensioncontroller.feature_controller.presentation.controlscreen.components.PresetButtonState
import com.dikoresearchsuspensioncontroller.feature_controller.presentation.controlscreen.components.PresetFloatingButtonState
import com.welie.blessed.ConnectionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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

    private val _showControlGroup = mutableStateOf(false)
    val showControlGroup: State<Boolean> = _showControlGroup

    private val _showRegulationGroup = mutableStateOf(false)
    val showRegulationGroup: State<Boolean> = _showRegulationGroup

    private val _isRegulating = mutableStateOf(false)
    val isRegulating: State<Boolean> = _isRegulating

    private val _presetState = mutableStateOf(PresetState())
    val presetState: State<PresetState> = _presetState

    val expandedState = mutableStateOf(PresetFloatingButtonState.COLLAPSED)
    val presetButton1State = mutableStateOf(PresetButtonState.COLLAPSED)
    val presetButton2State = mutableStateOf(PresetButtonState.COLLAPSED)
    val presetButton3State = mutableStateOf(PresetButtonState.COLLAPSED)


    private val _deviceMode = mutableStateOf(DeviceMode.DoubleWay().alias)
    val deviceMode: State<String> = _deviceMode

    private var selectedPressureSensor: PressureSensor = PressureSensor.China_0_20()
    private var selectedPressureUnits: PressureUnits = PressureUnits.Bar()

    private var deviceMacAddress = ""

    private var isScreenActive = false

    private var pressurePreset1 = "0,0,0,0"
    private var pressurePreset2 = "0,0,0,0"
    private var pressurePreset3 = "0,0,0,0"

    private var currentRawValues = SensorsRawValues()

    init {
        viewModelScope.launch {
            applicationSettingsFlow.collectLatest { settings ->
                deviceMacAddress = settings.deviceAddress
                selectedPressureSensor = settings.pressureSensorType
                selectedPressureUnits = settings.pressureUnits
                _showPressureInTank.value = settings.useTankPressure
                _deviceMode.value = settings.deviceMode.alias
                _showControlGroup.value = settings.showControlGroup
                _showRegulationGroup.value = settings.showRegulationGroup
                pressurePreset1 = settings.pressurePreset1
                pressurePreset2 = settings.pressurePreset2
                pressurePreset3 = settings.pressurePreset3
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
                if (isScreenActive){
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
        hideExpandedPresetGroup()
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
        viewModelScope.launch(Dispatchers.IO) {
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

    fun writeRegulationParams(params: PressureRegulationParameters){
        viewModelScope.launch(Dispatchers.IO) {
            suspensionControllerUseCases.writePressureRegulation(params)
                .fold(
                    {
                        Timber.e("Error while writing info")
                        viewModelScope.launch {
                            _eventFlow.emit(
                                UiEventControlScreen.ShowSnackbar("Error while writing regulation")
                            )
                        }
                    },
                    {

                    }
                )
        }
    }

    fun savePresetClicked(presetNum: Int){
        //hidePresetValues()
        //hideExpandedPresetGroup()

        viewModelScope.launch(Dispatchers.IO) {
            var preset = "0,0,0,0"
            when(_deviceMode.value){
                DeviceMode.SingleWay().alias -> {
                    if(currentRawValues.pressure1_mV > 6){
                        preset = "${currentRawValues.pressure1_mV},0,0,0"
                    }
                }
                DeviceMode.DoubleWay().alias -> {
                    if(currentRawValues.pressure1_mV > 6 && currentRawValues.pressure2_mV > 6){
                        preset = "${currentRawValues.pressure1_mV},${currentRawValues.pressure2_mV},0,0"
                    }
                }
                DeviceMode.QuadroWay().alias -> {
                    if(currentRawValues.pressure1_mV > 6 && currentRawValues.pressure2_mV > 6 &&
                        currentRawValues.pressure3_mV > 6 && currentRawValues.pressure4_mV > 6 ){
                        preset = "${currentRawValues.pressure1_mV},${currentRawValues.pressure2_mV},${currentRawValues.pressure3_mV},${currentRawValues.pressure4_mV}"
                    }
                }
                else -> {
                    preset = "0,0,0,0"
                }
            }
            Timber.i("Saving preset: $preset")
            dataStoreManager.setPressurePreset(
                presetNum,
                preset
            )
            viewModelScope.launch {
                _eventFlow.emit(
                    UiEventControlScreen.ShowSnackbar("Preset saved")
                )
            }
        }
    }

    fun selectPresetClicked(presetNum: Int){
        val presetToSend = when(presetNum){
            1 -> {
                pressurePreset1
            }
            2 -> {
                pressurePreset2
            }
            3 -> {
                pressurePreset3
            }
            else -> {
                "0,0,0,0"
            }
        }
        val presetPressure = presetToSend.split(",").map{it.toInt()}.toTypedArray()
        val sensorsValues = SensorsValues()
        val rawValues = SensorsRawValues(
            pressure1_mV = presetPressure[0],
            pressure2_mV = presetPressure[1],
            pressure3_mV = presetPressure[2],
            pressure4_mV = presetPressure[3],
        )

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
                _presetState.value = presetState.value.copy(
                    showPresetValues = true,
                    presetPressure1 = if(sensorsValues.pressure1 >= 0.0)  String.format("%.1f", sensorsValues.pressure1) else "----",
                    presetPressure2 = if(sensorsValues.pressure2 >= 0.0)  String.format("%.1f", sensorsValues.pressure2) else "----",
                    presetPressure3 = if(sensorsValues.pressure3 >= 0.0)  String.format("%.1f", sensorsValues.pressure3) else "----",
                    presetPressure4 = if(sensorsValues.pressure4 >= 0.0)  String.format("%.1f", sensorsValues.pressure4) else "----",
                )
            }
            is PressureUnits.Psi -> {
                sensorsValues.convertToPsi()
                //Timber.i("Sensors data: $sensorsValues")
                _presetState.value = presetState.value.copy(
                    showPresetValues = true,
                    presetPressure1 = if(sensorsValues.pressure1 >= 0.0)  String.format("%.0f", sensorsValues.pressure1) else "----",
                    presetPressure2 = if(sensorsValues.pressure2 >= 0.0)  String.format("%.0f", sensorsValues.pressure2) else "----",
                    presetPressure3 = if(sensorsValues.pressure3 >= 0.0)  String.format("%.0f", sensorsValues.pressure3) else "----",
                    presetPressure4 = if(sensorsValues.pressure4 >= 0.0)  String.format("%.0f", sensorsValues.pressure4) else "----",
                )
            }
        }
    }

    fun hidePresetValues(){
        _presetState.value = presetState.value.copy(
            showPresetValues = false
        )
    }

    fun hideExpandedPresetGroup(){
        hidePresetValues()
        expandedState.value = PresetFloatingButtonState.COLLAPSED
        presetButton1State.value = PresetButtonState.COLLAPSED
        presetButton2State.value = PresetButtonState.COLLAPSED
        presetButton3State.value = PresetButtonState.COLLAPSED
    }

    fun sendPresetClicked(presetNum: Int){
        hidePresetValues()
        hideExpandedPresetGroup()
        val presetToSend = when(presetNum){
            1 -> {
                pressurePreset1
            }
            2 -> {
                pressurePreset2
            }
            3 -> {
                pressurePreset3
            }
            else -> {
                "0,0,0,0"
            }
        }
        //TODO adapt to current control params
        val waysType = "DOUBLE"
        val airPreparingType = "RECEIVER"

        //TODO add cast check
        val presetPressure = presetToSend.split(",").map{it.toInt()}.toTypedArray()
        Timber.i("Sending params $presetPressure")

        val paramsToSend = PressureRegulationParameters(
            commandType = "START",
            waysType = waysType,
            airPreparingType = airPreparingType,
            useTankPressure = false,
            airPressureSensorType = "None",
            refPressure1mV = presetPressure[0],
            refPressure2mV = presetPressure[1],
            refPressure3mV = presetPressure[2],
            refPressure4mV = presetPressure[3]
        )

        Timber.i("Params object: $paramsToSend")

        writeRegulationParams(paramsToSend)

        viewModelScope.launch {
            _eventFlow.emit(
                UiEventControlScreen.ShowSnackbar("Preset sent")
            )
        }

    }

    fun setScreenActive(status: Boolean){
        isScreenActive = status
    }

    fun startReadingSensorsValues(){
        if (readingJob != null) return

        if (suspensionControllerUseCases.getPeripheralConnectionState() != ConnectionState.CONNECTED){
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            suspensionControllerUseCases.startObserveNotification { value ->
                viewModelScope.launch {
                    _eventFlow.emit(
                        UiEventControlScreen.ShowSnackbar("Got notification")
                    )
                }
            }
        }

        readingJob = viewModelScope.launch(Dispatchers.IO) {
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
                            currentRawValues = rawValues.copy()
                            //Timber.i("Sensors raw data: $rawValues")
                            when(selectedPressureSensor){
                                is PressureSensor.China_0_20 -> {
                                    sensorsValues.calculateFromChinaSensor(rawValues)
                                }
                                is PressureSensor.Catterpillar -> {
                                    sensorsValues.calculateFromCaterpillarSensor(rawValues)
                                }
                            }

                            Timber.i("Flag is ${rawValues.flag}")
                            _isRegulating.value = rawValues.flag > 0

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
                delay(200)
            }
        }
    }

    fun stopReadingSensorsValues(){
        if (readingJob != null){
            println("cancelling Job")
            readingJob?.cancel()
            readingJob = null
        }
        viewModelScope.launch(Dispatchers.IO) {
            suspensionControllerUseCases.stopObserveNotification()
        }
    }
}