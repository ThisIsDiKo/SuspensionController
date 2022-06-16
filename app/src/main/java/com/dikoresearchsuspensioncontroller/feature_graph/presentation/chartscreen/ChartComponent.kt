package com.dikoresearchsuspensioncontroller.feature_graph.presentation.chartscreen

import android.graphics.Rect
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

                if (state.showPressure1.value){
                    drawLine(
                        color = Color.Blue,
                        strokeWidth = 3.dp.value,
                        start = Offset(xFromOffset, yFromOffset1),
                        end = Offset(xToOffset, yToOffset1),
                    )
                }

                if (state.showPressure2.value){
                    drawLine(
                        color = Color.Cyan,
                        strokeWidth = 3.dp.value,
                        start = Offset(xFromOffset, yFromOffset2),
                        end = Offset(xToOffset, yToOffset2),
                    )
                }

                if (state.showPressure3.value){
                    drawLine(
                        color = Color.Green,
                        strokeWidth = 3.dp.value,
                        start = Offset(xFromOffset, yFromOffset3),
                        end = Offset(xToOffset, yToOffset3),
                    )
                }

                if (state.showPressure4.value){
                    drawLine(
                        color = Color.Magenta,
                        strokeWidth = 3.dp.value,
                        start = Offset(xFromOffset, yFromOffset4),
                        end = Offset(xToOffset, yToOffset4),
                    )
                }

            }

            if (state.showPressure1.value){
                drawCircle(
                    color = Color.Blue,
                    radius = 6.dp.value,
                    center = Offset(xFromOffset, yFromOffset1)
                )
            }

            if (state.showPressure2.value){
                drawCircle(
                    color = Color.Cyan,
                    radius = 6.dp.value,
                    center = Offset(xFromOffset, yFromOffset2)
                )
            }

            if (state.showPressure3.value){
                drawCircle(
                    color = Color.Green,
                    radius = 6.dp.value,
                    center = Offset(xFromOffset, yFromOffset3)
                )
            }

            if (state.showPressure4.value){
                drawCircle(
                    color = Color.Magenta,
                    radius = 6.dp.value,
                    center = Offset(xFromOffset, yFromOffset4)
                )
            }
        }
    }

//    BoxWithConstraints(
//        modifier = Modifier
//            .padding(8.dp)
//    ) {
//        val boxWithConstraintsScope = this
//
//    }
}