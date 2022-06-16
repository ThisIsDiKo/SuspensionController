package com.dikoresearchsuspensioncontroller.feature_graph.presentation.chartscreen

import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun ControlButtons(
    state: ChartState
){
    val currentContext = LocalContext.current

    Button(
        onClick = {
            state.startGenerator()
        }
    ) {
        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
    }
    Button(
        onClick = {
            state.stopGenerator()
        }
    ) {
        Icon(imageVector = Icons.Filled.Warning, contentDescription = null)
    }
    Button(
        onClick = {
            state.resetView()
        }
    ) {
        Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
    }
    IconButton(onClick = {

        state.saveData(currentContext)
    }) {
        Icon(imageVector = Icons.Default.Share, contentDescription = null)
    }
}