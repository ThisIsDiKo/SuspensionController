package com.dikoresearchsuspensioncontroller.feature_graph.presentation.chartscreen

import android.graphics.Rect
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.DecimalFormat
import kotlin.random.Random

@Composable
fun ChartScreen(sensorsFrames: List<SensorsFrame>){

    val state = rememberSaveable(saver = SensorsChartState.Saver) {
        SensorsChartState.getState(sensorsFrames)
    }

    val decimalFormat = DecimalFormat("##.00")
    val textPaint = Paint().asFrameworkPaint().apply {
        isAntiAlias = true
        textSize = 32.sp.value
        color = Color.Black.toArgb()
    }
    val bounds = Rect()


    Column(modifier = Modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
                .transformable(state.transformableState)
                .scrollable(state.scrollableState, orientation = Orientation.Horizontal)
        ){
            val chartXStartOffset = 128.dp.value
            val chartWidth = size.width - chartXStartOffset
            val chartHeight = size.height - 64.dp.value

            state.setViewSize(width = chartWidth, height = chartHeight)
            state.calculateGridWidth()


            //Horizontal Axes
            drawLine(
                color = Color.Black,
                strokeWidth = 6.dp.value,
                start = Offset(chartXStartOffset, chartHeight),
                end = Offset(chartWidth + chartXStartOffset, chartHeight),
            )

            //Pressure Lines
            state.pressureLines.value.forEachIndexed{index, value ->
                val yOffset = state.yOffset(value)
                val text = decimalFormat.format(value)

                if (index != state.pressureLines.value.size-1){
                    drawLine(
                        color = Color.Red,
                        strokeWidth = 4.dp.value,
                        start = Offset(chartXStartOffset, yOffset),
                        end = Offset(chartXStartOffset + chartWidth, yOffset),
                        pathEffect = PathEffect.dashPathEffect(intervals = floatArrayOf(10f, 20f), phase = 5f)
                    )
                }


                drawIntoCanvas {
                    textPaint.getTextBounds(text, 0, text.length, bounds)
                    val textHeight = bounds.height()
                    val textWidth = bounds.width()
                    it.nativeCanvas.drawText(
                        text,
                        chartXStartOffset - textWidth - 8.dp.value,
                        yOffset + textHeight / 2,
                        textPaint
                    )
                }
            }

            //Vertical Axes
            drawLine(
                color = Color.Black,
                strokeWidth = 6.dp.value,
                start = Offset(chartXStartOffset, 0f),
                end = Offset(chartXStartOffset, chartHeight),
            )

            //TimeLines
            state.timeLines.value.forEach{ frame ->
                val offset = state.xOffset(frame)
                if (offset !in 0f..chartWidth) return@forEach
                val text = decimalFormat.format(frame.timeStamp)

                drawLine(
                    color = Color.Green,
                    strokeWidth = 10.dp.value,
                    start = Offset(chartXStartOffset+offset, 0f),
                    end = Offset(chartXStartOffset+offset, chartHeight),
                    pathEffect = PathEffect.dashPathEffect(intervals = floatArrayOf(10f, 20f), phase = 5f)
                )

                drawIntoCanvas {
                    textPaint.getTextBounds(text, 0, text.length, bounds)
                    val textHeight = bounds.height()
                    val textWidth = bounds.width()
                    it.nativeCanvas.drawText(
                        text,
                        chartXStartOffset + offset - textWidth/2,
                        chartHeight + 8.dp.value + textHeight,
                        textPaint
                    )
                }
            }

            state.visibleSensorsFrames.value.forEachIndexed{index, sensorFrame ->
                val xOffset1 = state.xOffset(sensorFrame)
                val yOffset1 = state.yOffset(sensorFrame.pressure1)
                if (index < state.visibleSensorsFrames.value.size - 1){

                    val xOffset2 = state.xOffset(state.visibleSensorsFrames.value[index+1])
                    val yOffset2 = state.yOffset(state.visibleSensorsFrames.value[index+1].pressure1)

                    drawLine(
                        color = Color.Blue,
                        strokeWidth = 2.dp.value,
                        start = Offset(chartXStartOffset + xOffset1, yOffset1),
                        end = Offset(chartXStartOffset + xOffset2, yOffset2),
                    )
                }
                drawCircle(
                    color = Color.Red,
                    radius = 6.dp.value,
                    center = Offset(chartXStartOffset + xOffset1, yOffset1)
                )
            }

        }
    }
}


