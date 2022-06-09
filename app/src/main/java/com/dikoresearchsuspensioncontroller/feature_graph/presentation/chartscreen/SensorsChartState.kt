package com.dikoresearchsuspensioncontroller.feature_graph.presentation.chartscreen

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import timber.log.Timber
import kotlin.math.roundToInt

class SensorsChartState {
    private var sensorsFrames = listOf<SensorsFrame>()

    private var visibleSensorsFramesCount = mutableStateOf(60)
    private var scrollOffset = mutableStateOf(0f)

    private var viewWidth = 0f
    private var viewHeight = 0f

    val visibleSensorsFrames = derivedStateOf {
        if (sensorsFrames.isNotEmpty()){
            sensorsFrames.subList(
                fromIndex = scrollOffset.value.roundToInt().coerceAtLeast(0),
                toIndex = (scrollOffset.value.roundToInt() + visibleSensorsFramesCount.value).coerceAtMost(sensorsFrames.size)
            )
        }
        else {
            emptyList()
        }
    }

    val scrollableState = ScrollableState {
        scrollOffset.value = if (it > 0) {
            (scrollOffset.value - it.scrolledFrames).coerceAtLeast(0f)
        } else {
            (scrollOffset.value - it.scrolledFrames).coerceAtMost(sensorsFrames.lastIndex.toFloat())
        }
        it
    }

    private val maxPressure = derivedStateOf {
        visibleSensorsFrames.value.maxOfOrNull { it.pressure1 } ?: 0f
    }

    private val minPressure = derivedStateOf {
        visibleSensorsFrames.value.minOfOrNull { it.pressure1 } ?: 0f
    }

    private var framesInGrid = Float.MAX_VALUE
    val timeLines = mutableStateOf(listOf<SensorsFrame>())

    val transformableState = TransformableState { zoomChange, offset, _ ->
        val newVisibleCount = (visibleSensorsFramesCount.value / zoomChange).roundToInt()
        visibleSensorsFramesCount.value = if (newVisibleCount > sensorsFrames.size) sensorsFrames.size else newVisibleCount
    }

    val pressureLines = derivedStateOf {
        val pressureLine = (maxPressure.value - minPressure.value) / 10
        mutableListOf<Float>().apply {
            repeat(11){
                 add (maxPressure.value - it * pressureLine)
            }
        }
    }

    fun calculateGridWidth(){
        val frameWidth = viewWidth / visibleSensorsFramesCount.value
        val currentGridWidth = framesInGrid * frameWidth

        when{
            currentGridWidth < MIN_GRID_WIDTH -> {
                framesInGrid = MAX_GRID_WIDTH / frameWidth
                timeLines.value = sensorsFrames.filterIndexed{index, _ ->
                    index % framesInGrid.roundToInt() == 0
                }
            }
            currentGridWidth > MAX_GRID_WIDTH -> {
                framesInGrid = MIN_GRID_WIDTH / frameWidth
                timeLines.value = sensorsFrames.filterIndexed{index, _ ->
                    index % framesInGrid.roundToInt() == 0
                }
            }
        }
    }

    fun setViewSize(width: Float, height: Float){
        viewWidth = width
        viewHeight = height
    }

    fun xOffset(frame: SensorsFrame) =
        viewWidth * visibleSensorsFrames.value.indexOf(frame).toFloat() / visibleSensorsFramesCount.value.toFloat()

    fun yOffset(value: Float) = viewHeight * (maxPressure.value - value) / (maxPressure.value - minPressure.value)

    private val Float.scrolledFrames: Float
        get() = this * visibleSensorsFramesCount.value.toFloat() / viewWidth

    companion object {
        private const val MAX_GRID_WIDTH = 500
        private const val MIN_GRID_WIDTH = 250
        private const val MAX_FRAMES = 100
        private const val MIN_FRAMES = 30
        private const val START_FRAMES = 60
        private const val FRAMES_COUNT = 10

        fun getState(sensorsFrames: List<SensorsFrame>, visibleSensorsFramesCount: Int? = null, scrollOffset: Float? = null) =
            SensorsChartState().apply {
                this.sensorsFrames = sensorsFrames
                this.visibleSensorsFramesCount.value = visibleSensorsFramesCount ?: START_FRAMES
                this.scrollOffset.value = scrollOffset
                    ?: (sensorsFrames.size.toFloat() - this.visibleSensorsFramesCount.value) //CHECK
            }

        @Suppress("UNCHECKED_CAST")
        val Saver: Saver<SensorsChartState, Any> = listSaver(
            save = { listOf(it.sensorsFrames, it.scrollOffset.value, it.visibleSensorsFramesCount.value)},
            restore = {
                getState(
                    sensorsFrames = it[0] as List<SensorsFrame>,
                    visibleSensorsFramesCount = it[2] as Int,
                    scrollOffset = it[1] as Float
                )
            }
        )
    }

}