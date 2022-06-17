package com.dikoresearchsuspensioncontroller.feature_graph.presentation.chartscreen

import android.content.Context
import android.os.Environment
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.File
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.random.Random

class ChartState(
    startFrames: List<SensorsFrame>,
    context: Context
) {

    private val sensorsFrames = mutableStateListOf<SensorsFrame>().apply {
        addAll(startFrames)
    }

    private val context = context

    //val visibleSensorsFrame = mutableStateListOf<SensorsFrame>()

    private var viewWidth = 0f
    private var viewHeight = 0f

    val showmV = mutableStateOf(false)
    val showPressure1 = mutableStateOf(true)
    val showPressure2 = mutableStateOf(true)
    val showPressure3 = mutableStateOf(true)
    val showPressure4 = mutableStateOf(true)
    val isScanning = mutableStateOf(false)



    var minTime = 0f
    var maxTime = 10000f

    private val visibleMinTime = mutableStateOf(0f)
    private val mainDivisionXAxes = mutableStateOf(1000f)

    val isZoomed = mutableStateOf(false)

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

    private var generatorJob: Job? = null



    val transformableState = TransformableState{ zoomChange, _, _ ->
        isZoomed.value = true
        var newDivision = (mainDivisionXAxes.value / zoomChange).coerceAtLeast(10f)
        //Timber.d("New zoomChange $zoomChange and division $newDivision")
        //newDivision = ((newDivision.roundToInt() / 10) * 10).toFloat()
        mainDivisionXAxes.value = newDivision
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

            val tempFullLust = mutableListOf<SensorsFrame>()
            for (i in sensorsFrames.indices){
                if (showmV.value){
                    tempFullLust.add(sensorsFrames[i])
                }
                else {
                    val k = 7.24f / 1000f
                    val b = -2.46f

                    val p1 = if (sensorsFrames[i].pressure1 >= 6.0f) k * sensorsFrames[i].pressure1 + b else 0.0f
                    val p2 = if (sensorsFrames[i].pressure2 >= 6.0f) k * sensorsFrames[i].pressure2 + b else 0.0f
                    val p3 = if (sensorsFrames[i].pressure3 >= 6.0f) k * sensorsFrames[i].pressure3 + b else 0.0f
                    val p4 = if (sensorsFrames[i].pressure4 >= 6.0f) k * sensorsFrames[i].pressure4 + b else 0.0f

                    tempFullLust.add(SensorsFrame(sensorsFrames[i].timeStamp, p1, p2, p3, p4))
                }

            }

//            val startIndex = sensorsFrames.indexOfFirst { it.timeStamp >= visibleMinTime.value }
//            val endIndex = sensorsFrames.indexOfLast { it.timeStamp <= visibleMaxTime.value }
//            if (startIndex < 0 || endIndex < 0){
//                return@derivedStateOf emptyList<SensorsFrame>()
//            }

            val endIndex = tempFullLust.indexOfLast { it.timeStamp <= visibleMaxTime.value }
            val startIndex = tempFullLust.indexOfFirst { it.timeStamp >= visibleMinTime.value }
            if (startIndex < 0 || endIndex < 0 || startIndex > endIndex){
                return@derivedStateOf emptyList<SensorsFrame>()
            }

            val temp = tempFullLust.subList(
                fromIndex = startIndex,
                toIndex = endIndex
            ).toMutableList().also {
                if (it.size == 0) return@also
                if (it.first().timeStamp > visibleMinTime.value){
                    if (startIndex > 0){
//                        val b = sensorsFrames[startIndex]
//                        val a = sensorsFrames[startIndex-1]
//
//                        var k = (b.pressure1 - a.pressure1) / (b.timeStamp - a.timeStamp)
//                        var s = a.pressure1 - k * a.timeStamp

                        val t = visibleMinTime.value
                        val p1 = calculateApprox(
                            fromX = tempFullLust[startIndex-1].timeStamp,
                            fromY = tempFullLust[startIndex-1].pressure1,
                            toX = tempFullLust[startIndex].timeStamp,
                            toY = tempFullLust[startIndex].pressure1,
                            x = t
                        )
                        val p2 = calculateApprox(
                            fromX = tempFullLust[startIndex-1].timeStamp,
                            fromY = tempFullLust[startIndex-1].pressure2,
                            toX = tempFullLust[startIndex].timeStamp,
                            toY = tempFullLust[startIndex].pressure2,
                            x = t
                        )
                        val p3 = calculateApprox(
                            fromX = tempFullLust[startIndex-1].timeStamp,
                            fromY = tempFullLust[startIndex-1].pressure3,
                            toX = tempFullLust[startIndex].timeStamp,
                            toY = tempFullLust[startIndex].pressure3,
                            x = t
                        )
                        val p4 = calculateApprox(
                            fromX = tempFullLust[startIndex-1].timeStamp,
                            fromY = tempFullLust[startIndex-1].pressure4,
                            toX = tempFullLust[startIndex].timeStamp,
                            toY = tempFullLust[startIndex].pressure4,
                            x = t
                        )

                        it.add(0, SensorsFrame(t, p1, p2, p3, p4))
                    }

                }
                if (it.last().timeStamp < visibleMaxTime.value){
                    if (endIndex < tempFullLust.size - 1){
                        val t = visibleMaxTime.value
                        val p1 = calculateApprox(
                            fromX = tempFullLust[endIndex].timeStamp,
                            fromY = tempFullLust[endIndex].pressure1,
                            toX = tempFullLust[endIndex+1].timeStamp,
                            toY = tempFullLust[endIndex+1].pressure1,
                            x = t
                        )
                        val p2 = calculateApprox(
                            fromX = tempFullLust[endIndex].timeStamp,
                            fromY = tempFullLust[endIndex].pressure2,
                            toX = tempFullLust[endIndex+1].timeStamp,
                            toY = tempFullLust[endIndex+1].pressure2,
                            x = t
                        )
                        val p3 = calculateApprox(
                            fromX = tempFullLust[endIndex].timeStamp,
                            fromY = tempFullLust[endIndex].pressure3,
                            toX = tempFullLust[endIndex+1].timeStamp,
                            toY = tempFullLust[endIndex+1].pressure3,
                            x = t
                        )
                        val p4 = calculateApprox(
                            fromX = tempFullLust[endIndex].timeStamp,
                            fromY = tempFullLust[endIndex].pressure4,
                            toX = tempFullLust[endIndex+1].timeStamp,
                            toY = tempFullLust[endIndex+1].pressure4,
                            x = t
                        )

                        it.add(SensorsFrame(t, p1, p2, p3, p4))

                    }
                }
            }
            temp
//            sensorsFrames.filter { frame ->
//                (frame.timeStamp <= visibleMaxTime.value && frame.timeStamp >= visibleMinTime.value)
//            }
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

        if (showPressure1.value){
            maxValue = maxOf(maxValue, visibleSensorsFrames.value.maxOfOrNull { it.pressure1 } ?: 0f)
        }
        if (showPressure2.value){
            maxValue = maxOf(maxValue, visibleSensorsFrames.value.maxOfOrNull { it.pressure2 } ?: 0f)
        }
        if (showPressure3.value){
            maxValue = maxOf(maxValue, visibleSensorsFrames.value.maxOfOrNull { it.pressure3} ?: 0f)
        }
        if (showPressure4.value){
            maxValue = maxOf(maxValue, visibleSensorsFrames.value.maxOfOrNull { it.pressure4 } ?: 0f)
        }
        maxValue
//        maxOf(
//            visibleSensorsFrames.value.maxOfOrNull { it.pressure1 } ?: 0f,
//            visibleSensorsFrames.value.maxOfOrNull { it.pressure2 } ?: 0f,
//            visibleSensorsFrames.value.maxOfOrNull { it.pressure3 } ?: 0f,
//            visibleSensorsFrames.value.maxOfOrNull { it.pressure4 } ?: 0f
//        )
    }

    private val minValue = derivedStateOf {
        //visibleSensorsFrames.value.minOfOrNull { it.pressure1 } ?: 0f
        var minValue = Float.MAX_VALUE
        if (showPressure1.value){
            minValue = minOf(minValue, visibleSensorsFrames.value.minOfOrNull { it.pressure1 } ?: 0f)
        }
        if (showPressure2.value){
            minValue = minOf(minValue, visibleSensorsFrames.value.minOfOrNull { it.pressure2 } ?: 0f)
        }
        if (showPressure3.value){
            minValue = minOf(minValue, visibleSensorsFrames.value.minOfOrNull { it.pressure3} ?: 0f)
        }
        if (showPressure4.value){
            minValue = minOf(minValue, visibleSensorsFrames.value.minOfOrNull { it.pressure4 } ?: 0f)
        }
        minValue
//        minOf(
//            visibleSensorsFrames.value.minOfOrNull { it.pressure1 } ?: 0f,
//            visibleSensorsFrames.value.minOfOrNull { it.pressure2 } ?: 0f,
//            visibleSensorsFrames.value.minOfOrNull { it.pressure3 } ?: 0f,
//            visibleSensorsFrames.value.minOfOrNull { it.pressure4 } ?: 0f
//        )
    }


    //val yDivisionLines = mutableStateOf(listOf(0f, 1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f, 10f))
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

    fun xOffset(frame: SensorsFrame) =
        viewWidth * (frame.timeStamp - visibleMinTime.value) / (visibleMaxTime.value - visibleMinTime.value)

    fun yOffset(value: Float) =
        viewHeight - viewHeight * (value - minValue.value) / (maxValue.value - minValue.value)

    fun Float.roundToTens() = ((this.roundToInt() / 10) * 10).toFloat()

    private fun calculateApprox(fromX: Float, fromY: Float, toX: Float, toY: Float, x: Float): Float{
        val k = (toY - fromY) / (toX - fromX)
        val s = fromY - k * fromX

        return k * x + s
    }

    fun startGenerator(){
        if (generatorJob == null){
            val timeOffset = if (sensorsFrames.isNotEmpty()){
                sensorsFrames.last().timeStamp
            } else {
                0f
            }
            val startTime = System.currentTimeMillis()

            generatorJob = CoroutineScope(Dispatchers.IO).launch {
                while(isActive){
                    val timeStamp = timeOffset + (System.currentTimeMillis() - startTime)
                    val p1 = 1000 + Random.nextFloat() * 500
                    val p2 = 1200 + Random.nextFloat() * 1000
                    val p3 = 1500 + Random.nextFloat() * 500
                    val p4 = 2000 + Random.nextFloat() * 100
                    sensorsFrames.add(SensorsFrame(timeStamp, p1, p2, p3, p4))
                    delay(100)
                }
            }
        }
        isScanning.value = true
    }

    fun stopGenerator(){
        if (generatorJob != null){
            generatorJob?.cancel()
            generatorJob = null
        }
        isScanning.value = false
    }

    fun resetView(){
        Timber.i("Reset view clicked")
        isZoomed.value = false
    }

    fun saveData(){
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "testGraph.txt")

        file.createNewFile()
        Timber.i("Saving file to ${file.absoluteFile}")
        var msg = "timestamp,pressure1,pressure2\n"
        for (frame in sensorsFrames){
            msg += "${frame.timeStamp},${frame.pressure1},${frame.pressure2}\n"

        }
        file.writeText(msg)
    }
}