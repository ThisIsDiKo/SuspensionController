package com.dikoresearchsuspensioncontroller.feature_controller.presentation.startscreen

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import timber.log.Timber

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun StartScreen(
    navController: NavController,
    viewModel: StartScreenViewModel = hiltViewModel()
){
    val showProgressBar = viewModel.showConnectionProgressBar
    val showStartButton = viewModel.showStartButton
    val showReconnectButton = viewModel.showReconnectButton

    val showPermissionInfoDialog = remember { mutableStateOf(false)}

    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    )

    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    DisposableEffect(
        key1 = lifecycleOwner,
        effect = {
            val observer = LifecycleEventObserver{ _, event ->
                if (event == Lifecycle.Event.ON_RESUME){
                    Timber.i("On Resumed")
                    val bluetoothManager: BluetoothManager? = context.getSystemService(BluetoothManager::class.java)
                    val bluetoothAdapter: BluetoothAdapter? = bluetoothManager?.adapter
                    if (bluetoothAdapter != null) {
                        if (bluetoothAdapter.isEnabled){
                            if (permissionState.allPermissionsGranted){
                                viewModel.startConnection()
                            }
                            else{
                                showPermissionInfoDialog.value = true
                            }
                        }
                        else {
                            Toast.makeText(context, "Need to turn on Bluetooth adapter", Toast.LENGTH_LONG).show()
                        }
                    }
                }
                else if (event == Lifecycle.Event.ON_PAUSE){
                    Timber.i("On Paused")
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Hello")
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate("settingsscreen")
                    }) {
                        Icon(Icons.Filled.Settings, contentDescription = "")
                    }
                },
                backgroundColor = Color.White
            )
        },
    ){ padding ->
        if(showPermissionInfoDialog.value){
            AlertDialog(
                onDismissRequest = {
//                    permissionState.launchMultiplePermissionRequest()
                    showPermissionInfoDialog.value = false
                },
                title = {
                    Text(text = "Location permission required")
                },
                text = {
                    Text("Starting from Android M (6.0), the system requires apps to be granted" +
                            "location access in order to scan for BLE devices.")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showPermissionInfoDialog.value = false
                            permissionState.launchMultiplePermissionRequest()
                        }) {
                        Text("Ok")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            showPermissionInfoDialog.value = false
                        }) {
                        Text("Cancel")
                    }
                }
            )
        }

        Column(
            modifier = Modifier.fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            if (showProgressBar.value){
                CircularProgressIndicator()
            }

            if (showStartButton.value){
                Spacer(modifier = Modifier.height(20.dp))
                Button(onClick = {
                    viewModel.onStartButtonClicked()
                }
                ) {
                    Text(text = "Start scan")
                }
            }

            if (showReconnectButton.value){
                Spacer(modifier = Modifier.height(20.dp))
                Button(onClick = {
                    viewModel.onReconnectButtonClicked()
                }
                ) {
                    Text(text = "Reconnect")
                }
            }
        }
    }
}