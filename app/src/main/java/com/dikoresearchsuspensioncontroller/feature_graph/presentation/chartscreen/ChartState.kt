package com.dikoresearchsuspensioncontroller.feature_graph.presentation.chartscreen

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf

class ChartState {
    private var sensorsFrames = listOf<SensorsFrame>()

    //val visibleSensorsFrame = mutableStateListOf<SensorsFrame>()

    private var viewWidth = 0f
    private var viewHeight = 0f

    var visibleMinTime = 0f
    var visibleMaxTime = 10000f
    var minTime = 0f
    var maxTime = 10000f

    val visibleSensorsFrames = derivedStateOf {
        if (sensorsFrames.isNotEmpty()){
            sensorsFrames.filter { frame ->
                (frame.timeStamp < visibleMaxTime && frame.timeStamp > visibleMinTime)
            }
        }
        else {
            emptyList()
        }
    }

    private val maxValue = derivedStateOf {
        visibleSensorsFrames.value.maxOfOrNull { it.pressure1 } ?: 0f
    }

    private val minValue = derivedStateOf {
        visibleSensorsFrames.value.minOfOrNull { it.pressure1 } ?: 0f
    }

    private val timeGrid = derivedStateOf {
        (visibleMaxTime - visibleMinTime) / 10f
    }

    private val mainDivisionXAxes = derivedStateOf {
        visibleMaxTime - visibleMinTime
    }
    private val yLines = mutableStateOf(listOf<Float>())
    private val xLines = derivedStateOf {
        mutableListOf<Float>().apply {
            repeat(11){
                add(visibleMinTime + it * mainDivisionXAxes.value)
            }
        }
    }
}