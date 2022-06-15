package com.dikoresearchsuspensioncontroller.feature_graph.presentation.chartscreen

import android.graphics.Rect
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

@Composable
fun ChartScreenNew(){
    val state = remember {
        val startList = mutableListOf<SensorsFrame>()
        var prevTimeStamp = 0f
        for(i in 0..100){
            val timeStamp = prevTimeStamp
            prevTimeStamp += Random.nextInt(2, 100)
            val p1 = 5 + Random.nextFloat() * 5
            val p2 = 1 + Random.nextFloat() * 1
            val p3 = 8 + Random.nextFloat() * 2
            val p4 = 3 + Random.nextFloat() * 2
            startList.add(SensorsFrame(timeStamp, p1, p2, p3, p4))
        }
        ChartState(startList)
    }

    val showPressure1 = remember { mutableStateOf(true)}
    val showPressure2 = remember { mutableStateOf(true)}
    val showPressure3 = remember { mutableStateOf(true)}
    val showPressure4= remember { mutableStateOf(true)}

    val textPaint = Paint().asFrameworkPaint().apply {
        isAntiAlias = true
        textSize = 32.sp.value
        color = Color.Black.toArgb()
    }
    val bounds = Rect()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ){
            Text("Show pressure 1")
            Switch(
                checked = showPressure1.value,
                onCheckedChange = {
                    state.showPressure1.value = it
                    showPressure1.value = !showPressure1.value
                }
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ){
            Text("Show pressure 2")
            Switch(
                checked = showPressure2.value,
                onCheckedChange = {
                    state.showPressure2.value = it
                    showPressure2.value = !showPressure2.value
                }
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ){
            Text("Show pressure 3")
            Switch(
                checked = showPressure3.value,
                onCheckedChange = {
                    state.showPressure3.value = it
                    showPressure3.value = !showPressure3.value
                }
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ){
            Text("Show pressure 4")
            Switch(
                checked = showPressure4.value,
                onCheckedChange = {
                    state.showPressure4.value = it
                    showPressure4.value = !showPressure4.value
                }
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ){
            Button(
                onClick = {
                    state.startGenerator()
                }
            ) {
                Text("Start generator")
            }
            Button(
                onClick = {
                    state.stopGenerator()
                }
            ) {
                Text("Stop generator")
            }
            Button(
                onClick = {
                    state.resetView()
                }
            ) {
                Text("Reset View")
            }
        }
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(10.dp)
                .transformable(state.transformableState)
                .scrollable(state.scrollableState, orientation = Orientation.Horizontal),
        ){
            val xStartOffset = 128.dp.value
            val chartWidth = size.width - xStartOffset
            val chartHeight = size.height - 64.dp.value

            state.setViewSize(chartWidth, chartHeight)

            //Horizontal Axes
            drawLine(
                color = Color.Black,
                strokeWidth = 6.dp.value,
                start = Offset(xStartOffset, chartHeight),
                end = Offset(chartWidth + xStartOffset, chartHeight),
            )

            //main division x axes
            state.xDivisionLines.value.forEachIndexed{ index, value ->
                val step = chartWidth / 10f
                val text = value.toInt().toString()
                val offset = index * step

                if (index != 0){
                    drawLine(
                        color = Color.LightGray,
                        strokeWidth = 2.dp.value,
                        start = Offset(xStartOffset + offset, 0f),
                        end = Offset(xStartOffset + offset, chartHeight),
                        pathEffect = PathEffect.dashPathEffect(intervals = floatArrayOf(10f, 20f), phase = 5f)
                    )
                }

                drawIntoCanvas {
                    textPaint.getTextBounds(text, 0, text.length, bounds)
                    val textHeight = bounds.height()
                    val textWidth = bounds.width()
                    it.nativeCanvas.drawText(
                        text,
                        xStartOffset + offset - textWidth/2,
                        chartHeight + 8.dp.value + textHeight,
                        textPaint
                    )
                }
            }


            //Vertical Axes
            drawLine(
                color = Color.Black,
                strokeWidth = 6.dp.value,
                start = Offset(xStartOffset, 0f),
                end = Offset(xStartOffset, chartHeight),
            )

            //main division y axes
            state.yDivisionLines.value.forEachIndexed{ index, value ->
                val step = chartHeight / 10f
                val text = String.format("%.2f", value) //TODO switch to float mode
                val offset = (10 - index) * step

                if (index != 0){
                    drawLine(
                        color = Color.LightGray,
                        strokeWidth = 2.dp.value,
                        start = Offset(xStartOffset, offset),
                        end = Offset(xStartOffset + chartWidth, offset),
                        pathEffect = PathEffect.dashPathEffect(intervals = floatArrayOf(10f, 20f), phase = 5f)
                    )
                }

                drawIntoCanvas {
                    textPaint.getTextBounds(text, 0, text.length, bounds)
                    val textHeight = bounds.height()
                    val textWidth = bounds.width()
                    it.nativeCanvas.drawText(
                        text,
                        xStartOffset - textWidth - 8.dp.value,
                        offset + textHeight / 2,
                        textPaint
                    )
                }
            }

            //Pressure lines
            state.visibleSensorsFrames.value.forEachIndexed { index, sensorsFrame ->
                val xFromOffset = xStartOffset + state.xOffset(sensorsFrame)
                val yFromOffset1 = state.yOffset(sensorsFrame.pressure1)
                val yFromOffset2 = state.yOffset(sensorsFrame.pressure2)
                val yFromOffset3 = state.yOffset(sensorsFrame.pressure3)
                val yFromOffset4 = state.yOffset(sensorsFrame.pressure4)

                if (index < state.visibleSensorsFrames.value.size - 1){

                    val xToOffset = xStartOffset + state.xOffset(state.visibleSensorsFrames.value[index+1])
                    val yToOffset1 = state.yOffset(state.visibleSensorsFrames.value[index+1].pressure1)
                    val yToOffset2 = state.yOffset(state.visibleSensorsFrames.value[index+1].pressure2)
                    val yToOffset3 = state.yOffset(state.visibleSensorsFrames.value[index+1].pressure3)
                    val yToOffset4 = state.yOffset(state.visibleSensorsFrames.value[index+1].pressure4)

                    if (showPressure1.value){
                        drawLine(
                            color = Color.Blue,
                            strokeWidth = 3.dp.value,
                            start = Offset(xFromOffset, yFromOffset1),
                            end = Offset(xToOffset, yToOffset1),
                        )
                    }

                    if (showPressure2.value){
                        drawLine(
                            color = Color.Cyan,
                            strokeWidth = 3.dp.value,
                            start = Offset(xFromOffset, yFromOffset2),
                            end = Offset(xToOffset, yToOffset2),
                        )
                    }

                    if (showPressure3.value){
                        drawLine(
                            color = Color.Green,
                            strokeWidth = 3.dp.value,
                            start = Offset(xFromOffset, yFromOffset3),
                            end = Offset(xToOffset, yToOffset3),
                        )
                    }

                    if (showPressure4.value){
                        drawLine(
                            color = Color.Magenta,
                            strokeWidth = 3.dp.value,
                            start = Offset(xFromOffset, yFromOffset4),
                            end = Offset(xToOffset, yToOffset4),
                        )
                    }

                }

                if (showPressure1.value){
                    drawCircle(
                        color = Color.Blue,
                        radius = 6.dp.value,
                        center = Offset(xFromOffset, yFromOffset1)
                    )
                }

                if (showPressure2.value){
                    drawCircle(
                        color = Color.Cyan,
                        radius = 6.dp.value,
                        center = Offset(xFromOffset, yFromOffset2)
                    )
                }

                if (showPressure3.value){
                    drawCircle(
                        color = Color.Green,
                        radius = 6.dp.value,
                        center = Offset(xFromOffset, yFromOffset3)
                    )
                }

                if (showPressure4.value){
                    drawCircle(
                        color = Color.Magenta,
                        radius = 6.dp.value,
                        center = Offset(xFromOffset, yFromOffset4)
                    )
                }
            }
        }
    }
}