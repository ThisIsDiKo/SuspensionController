package com.dikoresearchsuspensioncontroller.feature_graph.presentation.chartscreen

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.dikoresearchsuspensioncontroller.feature_graph.presentation.chartscreen.components.*
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

@Composable
fun ChartScreenNew(
    navController: NavController,
    viewModel: ChartScreenViewModel = hiltViewModel()
){
    val scaffoldState = rememberScaffoldState()
    val configuration = LocalConfiguration.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val state = viewModel.chartState

    LaunchedEffect(key1 = true){
        viewModel.eventFlow.collectLatest { event ->
            when(event){
                is UiChartScreenEvents.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message
                    )
                }
                else -> {}
            }
        }
    }

    DisposableEffect(
        key1 = lifecycleOwner,
        effect = {
            val observer = LifecycleEventObserver{ _, event ->
                if (event == Lifecycle.Event.ON_RESUME){

                }
                else if (event == Lifecycle.Event.ON_STOP){
                    Timber.i("Chart screen stopped")
                }
                else if (event == Lifecycle.Event.ON_PAUSE){
                    Timber.i("Chart screen paused")
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    )

    Scaffold(
        scaffoldState = scaffoldState,
    ){ paddingValues ->

        FileNameDialog(
            enabled = viewModel.showFileNameDialog.value,
            onDismissRequest = {viewModel.hideFileNameDialog()},
            onAccept = {viewModel.saveFile(it)}
        )

        SaveProgressDialog(
            enabled = viewModel.isSavingFile.value
        )

        when (configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                Row(
                    modifier = Modifier.fillMaxSize()
                        .padding(paddingValues),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    ChartComponent(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        state = state
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(10.dp, 5.dp, 10.dp, 5.dp),
                        verticalArrangement = Arrangement.SpaceAround,
                        horizontalAlignment = Alignment.Start
                    ) {
                        SourceSelectionBlock(
                            showVoltageAlias = "mV",
                            showSource1Alias = "1",
                            showSource2Alias = "2",
                            showSource3Alias = "3",
                            showSource4Alias = "4",
                            showSource5Alias = "5",
                            showVoltage = viewModel.showRawSensors.value,
                            showSource1 = viewModel.showSensor1.value,
                            showSource2 = viewModel.showSensor2.value,
                            showSource3 = viewModel.showSensor3.value,
                            showSource4 = viewModel.showSensor4.value,
                            showSource5 = viewModel.showSensor5.value,
                            showSource1Color = Color.Red,
                            showSource2Color = Color.Blue,
                            showSource3Color = Color.Green,
                            showSource4Color = Color.Magenta,
                            showSource5Color = Color.Yellow,
                            onShowVoltageChanged = {
                                viewModel.showRawSensors.value = !viewModel.showRawSensors.value
                            },
                            onShowSource1Changed = {
                                viewModel.showSensor1.value = !viewModel.showSensor1.value
                            },
                            onShowSource2Changed = {
                                viewModel.showSensor2.value = !viewModel.showSensor2.value
                            },
                            onShowSource3Changed = {
                                viewModel.showSensor3.value = !viewModel.showSensor3.value
                            },
                            onShowSource4Changed = {
                                viewModel.showSensor4.value = !viewModel.showSensor4.value
                            },
                            onShowSource5Changed = {
                                viewModel.showSensor5.value = !viewModel.showSensor5.value
                            }
                        )
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(10.dp, 5.dp, 10.dp, 5.dp),
                        verticalArrangement = Arrangement.SpaceAround,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ControlButtons(
                            isScanning = viewModel.isScanning.value,
                            isZoomed = state.isZoomed.value,
                            startScan = viewModel::startDataRecording,
                            stopScan = viewModel::stopDataRecording,
                            resetView = viewModel::viewZoomOut,
                            saveToDisk = viewModel::showFileNameDialog
                        )
                    }
                }
            }
            else -> {
                Column(
                    modifier = Modifier.fillMaxSize()
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    ChartComponent(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        state = state
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp, 5.dp, 10.dp, 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        SourceSelectionBlock(
                            showVoltageAlias = "mV",
                            showSource1Alias = "1",
                            showSource2Alias = "2",
                            showSource3Alias = "3",
                            showSource4Alias = "4",
                            showSource5Alias = "5",
                            showVoltage = viewModel.showRawSensors.value,
                            showSource1 = viewModel.showSensor1.value,
                            showSource2 = viewModel.showSensor2.value,
                            showSource3 = viewModel.showSensor3.value,
                            showSource4 = viewModel.showSensor4.value,
                            showSource5 = viewModel.showSensor5.value,
                            showSource1Color = Color.Red,
                            showSource2Color = Color.Blue,
                            showSource3Color = Color.Green,
                            showSource4Color = Color.Magenta,
                            showSource5Color = Color.Yellow,
                            onShowVoltageChanged = {
                                viewModel.showRawSensors.value = !viewModel.showRawSensors.value
                            },
                            onShowSource1Changed = {
                                viewModel.showSensor1.value = !viewModel.showSensor1.value
                            },
                            onShowSource2Changed = {
                                viewModel.showSensor2.value = !viewModel.showSensor2.value
                            },
                            onShowSource3Changed = {
                                viewModel.showSensor3.value = !viewModel.showSensor3.value
                            },
                            onShowSource4Changed = {
                                viewModel.showSensor4.value = !viewModel.showSensor4.value
                            },
                            onShowSource5Changed = {
                                viewModel.showSensor5.value = !viewModel.showSensor5.value
                            }
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ){
                        ControlButtons(
                            isScanning = viewModel.isScanning.value,
                            isZoomed = state.isZoomed.value,
                            startScan = viewModel::startDataRecording,
                            stopScan = viewModel::stopDataRecording,
                            resetView = viewModel::viewZoomOut,
                            saveToDisk = viewModel::showFileNameDialog
                        )
                    }
                }
            }
        }
    }
}

