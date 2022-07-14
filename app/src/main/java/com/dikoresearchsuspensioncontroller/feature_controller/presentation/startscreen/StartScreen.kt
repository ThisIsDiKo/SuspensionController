package com.dikoresearchsuspensioncontroller.feature_controller.presentation.startscreen

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.dikoresearchsuspensioncontroller.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.flow.collectLatest
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

    val scaffoldState = rememberScaffoldState()

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
                                viewModel.readDeviceAddress()
                                //viewModel.startConnection()
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
                else if (event == Lifecycle.Event.ON_STOP){
                    Timber.i("On Stopped")
                }
                else if (event == Lifecycle.Event.ON_DESTROY){
                    Timber.i("On Destroy")
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    )

    LaunchedEffect(key1 = true){
        viewModel.eventFlow.collectLatest { event ->
            when(event){
                is UiEventStartScreen.NavigateTo -> {
                    navController.navigate(event.destination){
                        popUpTo("startscreen"){
                            inclusive = true
                        }
                    }
                }
                is UiEventStartScreen.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message
                    )
                }
            }
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {

                },
                actions = {
                    IconButton(onClick = {
                        //Такой переход вызывает событие onCleared() во viewModel
                        viewModel.onNavigateClicked()

                        //Такой переход не вызывает событие onCleared() во viewModel
                        //navController.navigate("settingsscreen")
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
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Icon(
                painter = painterResource(id = R.drawable.ic_air_bag),
                contentDescription = null,
                modifier = Modifier.size(200.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (showProgressBar.value){
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "Connecting to device ...")
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