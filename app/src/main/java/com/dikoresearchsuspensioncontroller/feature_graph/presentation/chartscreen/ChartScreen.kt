package com.dikoresearchsuspensioncontroller.feature_graph.presentation.chartscreen

import android.graphics.Rect
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
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
fun ChartScreen(dots: List<Float>){

    val decimalFormat = DecimalFormat("##.00")
    val textPaint = Paint().asFrameworkPaint().apply {
        isAntiAlias = true
        textSize = 32.sp.value
        color = Color.Black.toArgb()
    }
    val bounds = Rect()

    Column(modifier = Modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier.fillMaxWidth().height(200.dp)
        ){
            val maxDot = dots.maxOrNull() ?: 0f
            val minDot = dots.minOrNull() ?: 0f

            val yLines = derivedStateOf {
                val yLineStep = (maxDot - minDot) / 10f
                mutableListOf<Float>().apply {
                    repeat(10) { if (it > 0) add (maxDot - yLineStep * it)}
                }
            }

            val xLines = derivedStateOf {
                val xLineStep = 100f / 10f
                mutableListOf<Float>().apply {
                    repeat(10) {  add (xLineStep * it)}
                }
            }

            val startXOffset = 128.dp.value

            val totalDots = dots.size
            val chartWidth = size.width - 10.dp.value - startXOffset
            val chartHeight = size.height - 10.dp.value
            val lineDistance = chartWidth / (totalDots + 1)


            drawLine(
                color = Color.Black,
                strokeWidth = 2.dp.value,
                start = Offset(startXOffset, chartHeight),
                end = Offset(chartWidth+startXOffset, chartHeight),
            )

            drawLine(
                color = Color.Black,
                strokeWidth = 2.dp.value,
                start = Offset(startXOffset, 0f),
                end = Offset(startXOffset, chartHeight),
            )

            var currentLineDistance = startXOffset

            yLines.value.forEach{ value ->
                val yOffset = chartHeight * (maxDot - value) / (maxDot - minDot)
                val text = decimalFormat.format(value)
                drawLine(
                    color = Color.Red,
                    pathEffect = PathEffect.dashPathEffect(intervals = floatArrayOf(10f, 20f), phase = 5f),
                    start = Offset(startXOffset, yOffset),
                    end = Offset(chartWidth+startXOffset, yOffset)
                )
                drawIntoCanvas {
                    textPaint.getTextBounds(text, 0, text.length, bounds)
                    val textHeight = bounds.height()
                    it.nativeCanvas.drawText(
                        text,
                        8.dp.value,
                        yOffset + textHeight / 2,
                        textPaint
                    )
                }
            }

            xLines.value.forEach{ value ->
                val xOffset = startXOffset + chartWidth * value / 100f
                val text = decimalFormat.format(value)
                drawLine(
                    color = Color.Green,
                    pathEffect = PathEffect.dashPathEffect(intervals = floatArrayOf(10f, 20f), phase = 5f),
                    start = Offset(xOffset, 0f),
                    end = Offset(xOffset, chartHeight)
                )
                drawIntoCanvas {
                    textPaint.getTextBounds(text, 0, text.length, bounds)
                    val textWidth = bounds.width()
                    val textHeight = bounds.height()
                    it.nativeCanvas.drawText(
                        text,
                        xOffset - textWidth/2,
                        textHeight + chartHeight + 2.dp.value,
                        textPaint
                    )
                }
            }

            dots.forEachIndexed{index, dot ->
                if (totalDots >= index + 2){
                    drawLine(
                        color = Color.Blue,
                        strokeWidth = Stroke.DefaultMiter,
                        start = Offset(
                            x = currentLineDistance,
                            y = chartHeight - dot / 100f * chartHeight
                        ),
                        end = Offset(
                            x = currentLineDistance + lineDistance,
                            y = chartHeight - dots[index+1] / 100f * chartHeight
                        )
                    )
                }
                currentLineDistance += lineDistance
            }
        }
    }

}

@Preview(device = Devices.PIXEL_2)
@Composable
fun ChartScreenPreview(){
    val dots = mutableListOf<Float>().apply {
        repeat(100) {add(Random.nextFloat()*100f)}
    }
    ChartScreen(dots)
}

