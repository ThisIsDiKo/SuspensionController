package com.dikoresearchsuspensioncontroller.feature_graph.presentation.chartscreen

import android.content.res.Configuration
import android.graphics.Rect
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

@Composable
fun ChartScreenNew(){
    val configuration = LocalConfiguration.current
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
        ChartState(startList)
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ){

                        Checkbox(
                            checked = showmV.value,
                            onCheckedChange = {
                                state.showmV.value = it
                                showmV.value = !showmV.value
                            }
                        )
                        Text("Use mV")
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ){
                        Checkbox(
                            checked = showPressure1.value,
                            onCheckedChange = {
                                state.showPressure1.value = it
                                showPressure1.value = !showPressure1.value
                            }
                        )
                        Text("1")
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ){
                        Checkbox(
                            checked = showPressure2.value,
                            onCheckedChange = {
                                state.showPressure2.value = it
                                showPressure2.value = !showPressure2.value
                            }
                        )
                        Text("2")
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ){
                        Checkbox(
                            checked = showPressure3.value,
                            onCheckedChange = {
                                state.showPressure3.value = it
                                showPressure3.value = !showPressure3.value
                            }
                        )
                        Text("3")

                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ){
                        Checkbox(
                            checked = showPressure4.value,
                            onCheckedChange = {
                                state.showPressure4.value = it
                                showPressure4.value = !showPressure4.value
                            }
                        )
                        Text("4")

                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(10.dp, 5.dp, 10.dp, 5.dp),
                    verticalArrangement = Arrangement.SpaceAround,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ControlButtons(state = state)
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ){

                        Checkbox(
                            checked = showmV.value,
                            onCheckedChange = {
                                state.showmV.value = it
                                showmV.value = !showmV.value
                            }
                        )
                        Text("Use mV")
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ){
                        Checkbox(
                            checked = showPressure1.value,
                            onCheckedChange = {
                                state.showPressure1.value = it
                                showPressure1.value = !showPressure1.value
                            }
                        )
                        Text("1")
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ){
                        Checkbox(
                            checked = showPressure2.value,
                            onCheckedChange = {
                                state.showPressure2.value = it
                                showPressure2.value = !showPressure2.value
                            }
                        )
                        Text("2")
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ){
                        Checkbox(
                            checked = showPressure3.value,
                            onCheckedChange = {
                                state.showPressure3.value = it
                                showPressure3.value = !showPressure3.value
                            }
                        )
                        Text("3")

                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ){
                        Checkbox(
                            checked = showPressure4.value,
                            onCheckedChange = {
                                state.showPressure4.value = it
                                showPressure4.value = !showPressure4.value
                            }
                        )
                        Text("4")

                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ){
                    ControlButtons(state = state)
                }
            }
        }
    }


}