package com.dikoresearchsuspensioncontroller.feature_graph.presentation.chartscreen.components

import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dikoresearchsuspensioncontroller.feature_graph.presentation.chartscreen.ChartState

@Composable
fun ChartComponent(
    modifier: Modifier,
    state: ChartState,
){
    val textPaint = Paint().asFrameworkPaint().apply {
        isAntiAlias = true
        textSize = 32.sp.value
        color = Color.Black.toArgb()
    }
    val bounds = Rect()

    Canvas(
        modifier = modifier
            .padding(10.dp)
            .scrollable(state.scrollableState, orientation = Orientation.Horizontal)
            .transformable(state.transformableState)

    ){
        val xStartOffset = 128.dp.value
        val chartWidth = size.width - xStartOffset
        val chartHeight = size.height - 96.dp.value

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
                val path = Path()

                path.moveTo(xStartOffset + offset - textHeight, chartHeight + 8.dp.value + textWidth)
                path.lineTo(xStartOffset + offset, chartHeight + 8.dp.value)

                it.nativeCanvas.drawTextOnPath(
                    text,
                    path,
                    0f,
                    0f,
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
            val text = String.format("%.2f", value)
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
            val yFromOffset1 = state.yOffset(sensorsFrame.valuesToShow[0])
            val yFromOffset2 = state.yOffset(sensorsFrame.valuesToShow[1])
            val yFromOffset3 = state.yOffset(sensorsFrame.valuesToShow[2])
            val yFromOffset4 = state.yOffset(sensorsFrame.valuesToShow[3])
            val yFromOffset5 = state.yOffset(sensorsFrame.valuesToShow[4])

            if (index < state.visibleSensorsFrames.value.size - 1){

                val xToOffset = xStartOffset + state.xOffset(state.visibleSensorsFrames.value[index+1])
                val yToOffset1 = state.yOffset(state.visibleSensorsFrames.value[index+1].valuesToShow[0])
                val yToOffset2 = state.yOffset(state.visibleSensorsFrames.value[index+1].valuesToShow[1])
                val yToOffset3 = state.yOffset(state.visibleSensorsFrames.value[index+1].valuesToShow[2])
                val yToOffset4 = state.yOffset(state.visibleSensorsFrames.value[index+1].valuesToShow[3])
                val yToOffset5= state.yOffset(state.visibleSensorsFrames.value[index+1].valuesToShow[4])

                if (state.showSensor1.value){
                    drawLine(
                        color = Color.Red,
                        strokeWidth = 3.dp.value,
                        start = Offset(xFromOffset, yFromOffset1),
                        end = Offset(xToOffset, yToOffset1),
                    )
                }

                if (state.showSensor2.value){
                    drawLine(
                        color = Color.Blue,
                        strokeWidth = 3.dp.value,
                        start = Offset(xFromOffset, yFromOffset2),
                        end = Offset(xToOffset, yToOffset2),
                    )
                }

                if (state.showSensor3.value){
                    drawLine(
                        color = Color.Green,
                        strokeWidth = 3.dp.value,
                        start = Offset(xFromOffset, yFromOffset3),
                        end = Offset(xToOffset, yToOffset3),
                    )
                }

                if (state.showSensor4.value){
                    drawLine(
                        color = Color.Magenta,
                        strokeWidth = 3.dp.value,
                        start = Offset(xFromOffset, yFromOffset4),
                        end = Offset(xToOffset, yToOffset4),
                    )
                }

                if (state.showSensor5.value){
                    drawLine(
                        color = Color.Yellow,
                        strokeWidth = 3.dp.value,
                        start = Offset(xFromOffset, yFromOffset5),
                        end = Offset(xToOffset, yToOffset5),
                    )
                }

            }

            if (state.showSensor1.value){
                drawCircle(
                    color = Color.Red,
                    radius = 6.dp.value,
                    center = Offset(xFromOffset, yFromOffset1)
                )
            }

            if (state.showSensor2.value){
                drawCircle(
                    color = Color.Blue,
                    radius = 6.dp.value,
                    center = Offset(xFromOffset, yFromOffset2)
                )
            }

            if (state.showSensor3.value){
                drawCircle(
                    color = Color.Green,
                    radius = 6.dp.value,
                    center = Offset(xFromOffset, yFromOffset3)
                )
            }

            if (state.showSensor4.value){
                drawCircle(
                    color = Color.Magenta,
                    radius = 6.dp.value,
                    center = Offset(xFromOffset, yFromOffset4)
                )
            }

            if (state.showSensor5.value){
                drawCircle(
                    color = Color.Yellow,
                    radius = 6.dp.value,
                    center = Offset(xFromOffset, yFromOffset4)
                )
            }
        }

        drawIntoCanvas {
            val text = "t,msec"
            textPaint.getTextBounds(text, 0, text.length, bounds)
            val textHeight = bounds.height()
            val textWidth = bounds.width()
            it.nativeCanvas.drawText(
                text,
                xStartOffset + chartWidth/2 - textWidth/2,
                chartHeight + 80.dp.value,
                textPaint
            )
        }
    }
}