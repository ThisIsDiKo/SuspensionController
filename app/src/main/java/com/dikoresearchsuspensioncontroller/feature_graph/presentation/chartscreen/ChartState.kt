package com.dikoresearchsuspensioncontroller.feature_graph.presentation.chartscreen

import android.content.Context
import android.os.Environment
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.dikoresearchsuspensioncontroller.feature_controller.domain.model.controller_models.SensorsRawValues
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.File
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.random.Random

class ChartState(
    startFrames: List<SensorsFrame>,
    val showSensor1: MutableState<Boolean>,
    val showSensor2: MutableState<Boolean>,
    val showSensor3: MutableState<Boolean>,
    val showSensor4: MutableState<Boolean>,
    val showSensor5: MutableState<Boolean>,
    val useRawValues: MutableState<Boolean>
) {

    val sensorsFrames = mutableStateListOf<SensorsFrame>().apply {
        addAll(startFrames)
    }

    private var viewWidth = 0f
    private var viewHeight = 0f

//    val useRawValues = mutableStateOf(false)

//    val showSensor1 = mutableStateOf(true)
//    val showSensor2 = mutableStateOf(true)
//    val showSensor3 = mutableStateOf(true)
//    val showSensor4 = mutableStateOf(true)
//    val showSensor5 = mutableStateOf(true)

//    val isScanning = mutableStateOf(false)
    val isZoomed = mutableStateOf(false)


    //Initial state of xAxes
    private val visibleMinTime = mutableStateOf(0f)
    private val mainDivisionXAxes = mutableStateOf(1000f)


    private val visibleMaxTime = derivedStateOf {
        if (isZoomed.value){
            visibleMinTime.value + 10 * mainDivisionXAxes.value.roundToTens()
        }
        else {
            visibleMinTime.value = 0f
            mainDivisionXAxes.value = sensorsFrames[sensorsFrames.size-1].timeStamp / 10f
            visibleMinTime.value + 10 * mainDivisionXAxes.value.roundToTens()
        }
    }

    val transformableState = TransformableState{ zoomChange, _, _ ->
        isZoomed.value = true
        mainDivisionXAxes.value = (mainDivisionXAxes.value / zoomChange).coerceAtLeast(10f)
    }

    val scrollableState = ScrollableState { scrollDelta ->
        isZoomed.value = true
        val dT = scrollDelta / viewWidth * 10 * mainDivisionXAxes.value.roundToTens()
        visibleMinTime.value = if (scrollDelta > 0){
            (visibleMinTime.value - dT).coerceAtLeast(0f)
        }
        else {
            (visibleMinTime.value - dT).coerceAtMost(
                if (maxTimestamp.value > 10000f){
                    maxTimestamp.value
                }
                else{
                    10000f
                }
            )
        }
        scrollDelta
    }

    val visibleSensorsFrames = derivedStateOf {
        if (sensorsFrames.isNotEmpty()){

            val tempFullList = mutableListOf<ChartValues>()

            //Timber.d("++++++Visible Sensors Frames Recalculation: ${sensorsFrames.indices}")

            for (i in sensorsFrames.indices){
                if (useRawValues.value){
                    tempFullList.add(ChartValues(sensorsFrames[i].timeStamp, sensorsFrames[i].rawValues))
                }
                else {
                    tempFullList.add(ChartValues(sensorsFrames[i].timeStamp, sensorsFrames[i].pressureValues))
                }

//                for ( j in sensorsFrames[0].rawValues.indices){
//                    val k = 7.24f / 1000f
//                    val b = -2.46f
//                    sensorsFrames[i].pressureValues[j] = if (sensorsFrames[i].rawValues[j] >= 6.0f) k * sensorsFrames[i].rawValues[j] + b else 0.0f
//                }
            }

            val endIndex = tempFullList.indexOfLast { it.timeStamp <= visibleMaxTime.value }
            val startIndex = tempFullList.indexOfFirst { it.timeStamp >= visibleMinTime.value }

            if (startIndex < 0 || endIndex < 0 || startIndex > endIndex){
                return@derivedStateOf emptyList<ChartValues>()
            }

            val visibleFrames = tempFullList.subList(
                fromIndex = startIndex,
                toIndex = endIndex
            ).also {
                if (it.size == 0) return@also
                if (it.first().timeStamp > visibleMinTime.value){
                    if (startIndex > 0){
                        val t = visibleMinTime.value
                        val additionalFrame = Array(5){0.0f}
                        for (i in additionalFrame.indices){
                            additionalFrame[i] = calculateApprox(
                                fromX = tempFullList[startIndex-1].timeStamp,
                                fromY = tempFullList[startIndex-1].valuesToShow[i],
                                toX = tempFullList[startIndex].timeStamp,
                                toY = tempFullList[startIndex].valuesToShow[i],
                                x = t
                            )
                        }
                        it.add(0, ChartValues(t, additionalFrame.copyOf()))
                    }
                }

                if (it.last().timeStamp < visibleMaxTime.value){
                    if (endIndex < tempFullList.size - 1 && endIndex > 0){
                        val t = visibleMaxTime.value
                        val additionalFrame = Array(5){0.0f}
                        for (i in additionalFrame.indices){
                            additionalFrame[i] = calculateApprox(
                                fromX = tempFullList[endIndex].timeStamp,
                                fromY = tempFullList[endIndex].valuesToShow[i],
                                toX = tempFullList[endIndex+1].timeStamp,
                                toY = tempFullList[endIndex+1].valuesToShow[i],
                                x = t
                            )
                        }
                        it.add(ChartValues(t, additionalFrame.copyOf()))
                    }
                }
            }
            visibleFrames
        }
        else {
            emptyList()
        }
    }

    private val maxTimestamp = derivedStateOf {
        visibleSensorsFrames.value.maxOfOrNull { it.timeStamp } ?: 0f
    }

    private val maxValue = derivedStateOf {
        var maxValue = 0f

        if (showSensor1.value){
            maxValue = maxOf(maxValue, visibleSensorsFrames.value.maxOfOrNull { it.valuesToShow[0] } ?: 0f)
        }
        if (showSensor2.value){
            maxValue = maxOf(maxValue, visibleSensorsFrames.value.maxOfOrNull { it.valuesToShow[1] } ?: 0f)
        }
        if (showSensor3.value){
            maxValue = maxOf(maxValue, visibleSensorsFrames.value.maxOfOrNull { it.valuesToShow[2]} ?: 0f)
        }
        if (showSensor4.value){
            maxValue = maxOf(maxValue, visibleSensorsFrames.value.maxOfOrNull { it.valuesToShow[3] } ?: 0f)
        }
        if (showSensor5.value){
            maxValue = maxOf(maxValue, visibleSensorsFrames.value.maxOfOrNull { it.valuesToShow[4] } ?: 0f)
        }
        maxValue
    }

    private val minValue = derivedStateOf {
        var minValue = Float.MAX_VALUE
        if (showSensor1.value){
            minValue = minOf(minValue, visibleSensorsFrames.value.minOfOrNull { it.valuesToShow[0] } ?: 0f)
        }
        if (showSensor2.value){
            minValue = minOf(minValue, visibleSensorsFrames.value.minOfOrNull { it.valuesToShow[1] } ?: 0f)
        }
        if (showSensor3.value){
            minValue = minOf(minValue, visibleSensorsFrames.value.minOfOrNull { it.valuesToShow[2]} ?: 0f)
        }
        if (showSensor4.value){
            minValue = minOf(minValue, visibleSensorsFrames.value.minOfOrNull { it.valuesToShow[3] } ?: 0f)
        }
        if (showSensor5.value){
            minValue = minOf(minValue, visibleSensorsFrames.value.minOfOrNull { it.valuesToShow[4] } ?: 0f)
        }
        minValue
    }

    val yDivisionLines = derivedStateOf {
        val yLineStep = (maxValue.value - minValue.value) / 10
        mutableListOf<Float>().apply {
            repeat(11){
                add(minValue.value + it * yLineStep)
            }
        }
    }

    val xDivisionLines = derivedStateOf {
        mutableListOf<Float>().apply {
            repeat(11){
                add(visibleMinTime.value + it * mainDivisionXAxes.value.roundToTens())
            }
        }
    }

    fun setViewSize(width: Float, height: Float){
        viewWidth = width
        viewHeight = height
    }

    fun resetVisibleFrames(){
        isZoomed.value = false
    }


    fun xOffset(frame: ChartValues) =
        viewWidth * (frame.timeStamp - visibleMinTime.value) / (visibleMaxTime.value - visibleMinTime.value)

    fun yOffset(value: Float) =
        viewHeight - viewHeight * (value - minValue.value) / (maxValue.value - minValue.value)

    private fun Float.roundToTens() = ((this.roundToInt() / 10) * 10).toFloat()

    private fun calculateApprox(fromX: Float, fromY: Float, toX: Float, toY: Float, x: Float): Float{
        val k = (toY - fromY) / (toX - fromX)
        val s = fromY - k * fromX

        return k * x + s
    }


//    fun saveData(filename: String){
//        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "testGraph.txt")
//
//        file.createNewFile()
//        Timber.i("Saving file to ${file.absoluteFile}")
//        var msg = "timestamp,pressure1,pressure2\n"
////        for (frame in sensorsFrames){
////            msg += "${frame.timeStamp},${frame.pressure1},${frame.pressure2}\n"
////
////        }
//        file.writeText(msg)
//    }
}