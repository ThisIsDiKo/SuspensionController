package com.dikoresearchsuspensioncontroller.feature_controller.presentation.startscreen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class StartScreenViewModel @Inject constructor(
    private val suspensionControllerUseCases: SuspensionControllerUseCases,
    private val dataStoreManager: DataStoreRepository
): ViewModel() {

    private val applicationSettingsFlow = dataStoreManager.getApplicationSettingsFlow()

    private val _showStartButton = mutableStateOf(false)
    val showStartButton: State<Boolean> = _showStartButton

    private val _showReconnectButton = mutableStateOf(false)
    val showReconnectButton: State<Boolean> = _showReconnectButton

    private val _showConnectionProgressBar = mutableStateOf(false)
    val showConnectionProgressBar: State<Boolean> = _showConnectionProgressBar

    private val _eventFlow = MutableSharedFlow<UiEventStartScreen>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var deviceAddress = ""

    init {
        viewModelScope.launch {
            applicationSettingsFlow.collectLatest { settings ->
                deviceAddress = settings.deviceAddress
                Timber.i("Got device address from  settings: $deviceAddress")
            }
        }
    }

    fun onStartButtonClicked(){
        viewModelScope.launch {
            _eventFlow.emit(
                UiEventStartScreen.NavigateTo("scanscreen")
            )
        }
    }

    fun onReconnectButtonClicked(){
        viewModelScope.launch {
            _showReconnectButton.value = false
            _showConnectionProgressBar.value = true

            suspensionControllerUseCases.connectToPeripheral(deviceAddress)
                .fold(
                    {
                        _showConnectionProgressBar.value = false
                        _showReconnectButton.value = true
                        _eventFlow.emit(
                            UiEventStartScreen.MakeToast("Can't connect to device $deviceAddress")
                        )
                    },
                    {
                        _showConnectionProgressBar.value = false
                        _eventFlow.emit(
                            UiEventStartScreen.NavigateTo("controlscreen")
                        )
                    }
                )
        }
    }

    fun startConnection(){
        viewModelScope.launch {
            if (deviceAddress.isNotBlank()){
                _showStartButton.value = false
                _showConnectionProgressBar.value = true
                suspensionControllerUseCases.connectToPeripheral(deviceAddress)
                    .fold(
                        {
                            _showConnectionProgressBar.value = false
                            _showReconnectButton.value = true
                            _eventFlow.emit(
                                UiEventStartScreen.MakeToast("Can't connect to device $deviceAddress")
                            )
                        },
                        {
                            _showConnectionProgressBar.value = false
                            _eventFlow.emit(
                                UiEventStartScreen.NavigateTo("controlscreen")
                            )
                        }
                    )
            }
            else {
                _showStartButton.value = true
            }
        }
    }
}