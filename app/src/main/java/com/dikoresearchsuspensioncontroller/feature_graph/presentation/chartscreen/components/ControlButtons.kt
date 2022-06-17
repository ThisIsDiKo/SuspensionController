package com.dikoresearchsuspensioncontroller.feature_graph.presentation.chartscreen.components


import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.dikoresearchsuspensioncontroller.R
import timber.log.Timber

@Composable
fun ControlButtons(
    isScanning: Boolean,
    isZoomed: Boolean,
    startScan: () -> Unit,
    stopScan: () -> Unit,
    resetView: () -> Unit,
    saveToDisk: () -> Unit
){
    IconButton(
        onClick = {
            if (isScanning){
                stopScan()
            }
            else {
                startScan()
            }

        }
    ) {
        if (isScanning){
            Icon(painter = painterResource(id = R.drawable.ic_pause), contentDescription = null)
        }
        else {
            Icon(painter = painterResource(id = R.drawable.ic_refresh), contentDescription = null)
        }
    }

    IconButton(
        onClick = {
            Timber.e("zoomed out button clicked")
            resetView()
        },
        enabled = isZoomed
    ) {
        Icon(painter = painterResource(id = R.drawable.ic_zoom_out), contentDescription = null)
    }

    IconButton(
        onClick = {
            Timber.e("save button clicked")
            saveToDisk()
        },
        enabled = !isScanning
    ) {
        Icon(painter = painterResource(id = R.drawable.ic_save_disk), contentDescription = null)
    }
}