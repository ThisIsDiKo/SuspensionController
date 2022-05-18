package com.dikoresearchsuspensioncontroller.feature_controller.domain.model.controller_models

import kotlin.experimental.inv

class OutputsValue(
    private val arraySize: Int
) {
    var outputs = Array(arraySize){OutputState.LOW}

    fun setAllOutputs(state: OutputState){
        for (i in outputs.indices){
            outputs[i] = state
        }
    }

    fun setOutput(index: Int, state: OutputState){
        if (index >= 0 && index < outputs.size){
            outputs[index] = state
        }
    }

    fun convertOutputsToByteArray(): ByteArray {
        var outputsByte = 0

        for (i in outputs.indices){
            outputsByte = outputsByte or (outputs[i].toInt() shl i)
        }

        return byteArrayOf(
            outputsByte.toByte().inv(),
            (outputsByte shr 8).toByte().inv(),
            outputsByte.toByte(),
            (outputsByte shr 8).toByte()
        )
    }
}

enum class OutputState{
    LOW,
    HIGH
}

fun OutputState.toInt() = if (this == OutputState.LOW) 1 else 0