package com.dikoresearchsuspensioncontroller.feature_graph.presentation.chartscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import timber.log.Timber

@Composable
fun SimpleScreen(){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val count = rememberSaveable{ mutableStateOf(0)}
        Button(
            onClick = {
                count.value += 1
                println("Hello world")
                Timber.i("hello world")
            }
        ){
            Text("Hello ${count.value}")
        }
    }
}