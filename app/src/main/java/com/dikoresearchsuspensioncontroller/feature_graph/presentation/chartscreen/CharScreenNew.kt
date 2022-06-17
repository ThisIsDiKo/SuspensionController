package com.dikoresearchsuspensioncontroller.feature_graph.presentation.chartscreen

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.dikoresearchsuspensioncontroller.feature_graph.presentation.chartscreen.components.ChartComponent
import com.dikoresearchsuspensioncontroller.feature_graph.presentation.chartscreen.components.ControlButtons
import com.dikoresearchsuspensioncontroller.feature_graph.presentation.chartscreen.components.SourceSelectionBlock
import kotlinx.coroutines.flow.collect
import timber.log.Timber
import kotlin.random.Random

@Composable
fun ChartScreenNew(){
    val configuration = LocalConfiguration.current
    val localContext = LocalContext.current
    val state = remember {
        val startList = mutableListOf<SensorsFrame>()
        var prevTimeStamp = 0f
        for(i in 0..100){
            val timeStamp = prevTimeStamp
            prevTimeStamp += Random.nextInt(2, 100)
            val p1 = 1000 + Random.nextFloat() * 500
            val p2 = 1200 + Random.nextFloat() * 1000
            val p3 = 1500 + Random.nextFloat() * 500
            val p4 = 2000 + Random.nextFloat() * 100
            startList.add(SensorsFrame(timeStamp, p1, p2, p3, p4))
        }
        ChartState(startList, localContext)
    }

    val showPressure1 = remember { mutableStateOf(true)}
    val showPressure2 = remember { mutableStateOf(true)}
    val showPressure3 = remember { mutableStateOf(true)}
    val showPressure4= remember { mutableStateOf(true)}
    val showmV= remember { mutableStateOf(false)}



    when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            Row(
                modifier = Modifier.fillMaxSize(),
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
                        showVoltage = showmV.value,
                        showSource1 = showPressure1.value,
                        showSource2 = showPressure2.value,
                        showSource3 = showPressure3.value,
                        showSource4 = showPressure4.value,
                        showSource5 = true,
                        showSource1Color = Color.Red,
                        showSource2Color = Color.Blue,
                        showSource3Color = Color.Green,
                        showSource4Color = Color.Magenta,
                        showSource5Color = Color.Yellow,
                        onShowVoltageChanged = {
                            state.showmV.value = it
                            showmV.value = !showmV.value
                            Timber.e("checkbox  clicked")
                        },
                        onShowSource1Changed = {
                            state.showPressure1.value = it
                            showPressure1.value = !showPressure1.value
                        },
                        onShowSource2Changed = {
                            state.showPressure2.value = it
                            showPressure2.value = !showPressure2.value
                        },
                        onShowSource3Changed = {
                            state.showPressure3.value = it
                            showPressure3.value = !showPressure3.value
                        },
                        onShowSource4Changed = {
                            state.showPressure4.value = it
                            showPressure4.value = !showPressure4.value
                        },
                        onShowSource5Changed = {

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
                        isScanning = state.isScanning.value,
                        isZoomed = state.isZoomed.value,
                        startScan = state::startGenerator,
                        stopScan = state::stopGenerator,
                        resetView = { state.resetView() },
                        saveToDisk = state::saveData
                    )
                }
            }
        }
        else -> {
            Column(
                modifier = Modifier.fillMaxSize(),
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
                        showVoltage = showmV.value,
                        showSource1 = showPressure1.value,
                        showSource2 = showPressure2.value,
                        showSource3 = showPressure3.value,
                        showSource4 = showPressure4.value,
                        showSource5 = true,
                        showSource1Color = Color.Red,
                        showSource2Color = Color.Blue,
                        showSource3Color = Color.Green,
                        showSource4Color = Color.Magenta,
                        showSource5Color = Color.Yellow,
                        onShowVoltageChanged = {
                            state.showmV.value = it
                            showmV.value = !showmV.value
                            Timber.e("checkbox  clicked")
                        },
                        onShowSource1Changed = {
                            state.showPressure1.value = it
                            showPressure1.value = !showPressure1.value
                        },
                        onShowSource2Changed = {
                            state.showPressure2.value = it
                            showPressure2.value = !showPressure2.value
                        },
                        onShowSource3Changed = {
                            state.showPressure3.value = it
                            showPressure3.value = !showPressure3.value
                        },
                        onShowSource4Changed = {
                            state.showPressure4.value = it
                            showPressure4.value = !showPressure4.value
                        },
                        onShowSource5Changed = {

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
                        isScanning = state.isScanning.value,
                        isZoomed = state.isZoomed.value,
                        startScan = state::startGenerator,
                        stopScan = state::stopGenerator,
                        resetView = { state.resetView() },
                        saveToDisk = state::saveData
                    )
                }
            }
        }
    }
}

