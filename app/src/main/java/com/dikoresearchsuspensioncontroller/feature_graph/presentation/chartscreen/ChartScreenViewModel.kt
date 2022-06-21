package com.dikoresearchsuspensioncontroller.feature_graph.presentation.chartscreen


import android.os.Environment
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dikoresearchsuspensioncontroller.feature_controller.domain.repository.local.DataStoreRepository
import com.dikoresearchsuspensioncontroller.feature_controller.domain.usecases.suspensioncontroller.SuspensionControllerUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlin.random.Random

@HiltViewModel
class ChartScreenViewModel @Inject constructor(
    private val suspensionControllerUseCases: SuspensionControllerUseCases,
    private val dataStoreManager: DataStoreRepository
): ViewModel(){
    private var readingJob: Job? = null


    private val _eventFlow = MutableSharedFlow<UiChartScreenEvents>()
    val eventFlow: SharedFlow<UiChartScreenEvents> = _eventFlow.asSharedFlow()

    private val _showFileNameDialog = mutableStateOf(false)
    val showFileNameDialog: State<Boolean> = _showFileNameDialog

    private val _isScanning = mutableStateOf(false)
    val isScanning: State<Boolean> = _isScanning

    private val _isSavingFile = mutableStateOf(false)
    val isSavingFile: State<Boolean> = _isSavingFile

    val showRawSensors = mutableStateOf(false)
//    val showRawSensors: State<Boolean> = _showRawSensors

    val showSensor1 = mutableStateOf(true)
//    val showSensor1: State<Boolean> = _showSensor1

    val showSensor2 = mutableStateOf(true)
//    val showSensor2: State<Boolean> = _showSensor2

    val showSensor3 = mutableStateOf(true)
//    val showSensor3: State<Boolean> = _showSensor3

    val showSensor4 = mutableStateOf(true)
//    val showSensor4: State<Boolean> = _showSensor4

    val showSensor5 = mutableStateOf(true)
//    val showSensor5: State<Boolean> = _showSensor5

    val chartState = ChartState(
        emptyList(),
        showSensor1 = showSensor1,
        showSensor2 = showSensor2,
        showSensor3 = showSensor3,
        showSensor4 = showSensor4,
        showSensor5 = showSensor5,
        useRawValues = showRawSensors,
    )

    override fun onCleared() {
        super.onCleared()
        Timber.e("Chart screen view model cleared")
        stopDataRecording()
    }

    fun showFileNameDialog(){
        _showFileNameDialog.value = true
    }

    fun hideFileNameDialog(){
        _showFileNameDialog.value = false
    }

    fun viewZoomOut(){
        chartState.resetVisibleFrames()
    }

    fun saveFile(fileName: String){
        hideFileNameDialog()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isSavingFile.value = true
                var msg = "timestamp [msec]"
                for (i in chartState.sensorsFrames.first().rawValues.indices){
                    msg += ";raw $i [mV]"
                }
                for (i in chartState.sensorsFrames.first().rawValues.indices){
                    msg += ";pressure $i [bar]"
                }
                msg += "\n"
                for (i in chartState.sensorsFrames.indices){
                    val frame = chartState.sensorsFrames[i]
                    msg += "${frame.timeStamp.roundToInt()}"
                    for (i in frame.rawValues.indices){
                        msg += ";${frame.rawValues[i].roundToInt()}"
                    }
                    for (i in frame.pressureValues.indices){
                        msg += ";${String.format("%.2f", frame.pressureValues[i])}"
                    }
                    msg += "\n"
                }

                val fName = "$fileName.csv"
                val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fName)
                Timber.e("Saving file to ${file.absoluteFile}")
                file.createNewFile()
                file.writeText(msg)

                _eventFlow.emit(
                    UiChartScreenEvents.ShowSnackbar("file ${file.absoluteFile} saved")
                )
            }
            catch (e: Exception){
                Timber.e("Can't write file $e")
                _eventFlow.emit(
                    UiChartScreenEvents.ShowSnackbar("Can't write file ${e.toString()}")
                )
            }
            finally {
                _isSavingFile.value = false
            }
        }

    }

    fun startDataRecording(){
        if (readingJob == null){
            chartState.sensorsFrames.clear()
            var prevTimeStamp = 0.0f
            var prevStartTime = System.currentTimeMillis()
            _isScanning.value = true
            readingJob = viewModelScope.launch(Dispatchers.Default) {
                while(true){
                    suspensionControllerUseCases.readSensorsValues()
                        .fold(
                            {
                                Timber.e("Error while reading sensors")
                                viewModelScope.launch {
                                    _eventFlow.emit(
                                        UiChartScreenEvents.ShowSnackbar("Error while reading sensors")
                                    )
                                }
                                stopDataRecording()
                            },
                            {rawValues ->
                                val rawValuesArray = arrayOf(
                                    rawValues.pressure1_mV.toFloat(),
                                    rawValues.pressure2_mV.toFloat(),
                                    rawValues.pressure3_mV.toFloat(),
                                    rawValues.pressure4_mV.toFloat(),
                                    rawValues.pressure5_mV.toFloat(),
                                )
                                val pressureValues = rawValuesArray.map {rawValue ->
                                    //TODO need to select correct equation from settings
                                    val k = 7.24f / 1000f
                                    val b = -2.46f
                                    val p = if (rawValue >= 340f) k * rawValue + b else 0.0f
                                    if (p < 0.0f) 0.0f else p
                                }.toTypedArray()
                                val timeStamp = prevTimeStamp
                                val newFrame = SensorsFrame(timeStamp, pressureValues.copyOf(), rawValuesArray.copyOf())
                                chartState.sensorsFrames.add(newFrame)
                            }
                        )

//                    val timeStamp = prevTimeStamp
//                    prevTimeStamp += Random.nextInt(2, 100).toFloat()
//                    val p1 = 1000 + Random.nextInt(-500, 500)
//                    val p2 = 1200 + Random.nextInt(-1000, 1000)
//                    val p3 = 1500 + Random.nextInt(-500, 500)
//                    val p4 = 2000 + Random.nextInt(-500, 500)
//                    val p5 = 1500 + Random.nextInt(-1000, 1000)
//
//                    val rawValues = arrayOf(p1.toFloat(), p2.toFloat(), p3.toFloat(), p4.toFloat(), p5.toFloat())
//                    val pressureValues = rawValues.map {rawValue ->
//                        val k = 7.24f / 1000f
//                        val b = -2.46f
//                        if (rawValue >= 6.0f) k * rawValue + b else 0.0f
//                    }.toTypedArray()
//
//                    val newFrame = SensorsFrame(timeStamp, pressureValues.copyOf(), rawValues.copyOf())
//                    chartState.sensorsFrames.add(newFrame)

                    delay(100)
                    prevTimeStamp += System.currentTimeMillis() - prevStartTime
                    prevStartTime = System.currentTimeMillis()
                }
            }
        }
    }

    fun stopDataRecording(){
        if (readingJob != null){
            readingJob?.cancel()
            readingJob = null
            _isScanning.value = false
        }
    }
}