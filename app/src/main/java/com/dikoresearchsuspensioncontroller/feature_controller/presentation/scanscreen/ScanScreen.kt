package com.dikoresearchsuspensioncontroller.feature_controller.presentation.scanscreen

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.dikoresearchsuspensioncontroller.feature_controller.presentation.scanscreen.components.DeviceCard
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScanScreen(
    navController: NavController,
    viewModel: ScanScreenViewModel = hiltViewModel()
){
    val bleDevices = viewModel.bleDevices
    val showConnectionDialog = viewModel.showConnectionDialog

    val scaffoldState = rememberScaffoldState()

    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    LaunchedEffect(key1 = true){
        viewModel.eventFlow.collectLatest { event ->
            when(event){
                is UiEventScanScreen.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message
                    )
                }
                is UiEventScanScreen.NavigateTo -> {
                    navController.navigate(event.destination){
                        popUpTo("scanscreen"){
                            inclusive = true
                        }
                    }
                }
            }
        }
    }

    DisposableEffect(
        key1 = lifecycleOwner,
        effect = {
            val observer = LifecycleEventObserver{ _, event ->
                if (event == Lifecycle.Event.ON_RESUME){
                    viewModel.startScanning()
                    Timber.i("On Resumed")
                }
                else if (event == Lifecycle.Event.ON_PAUSE){
                    viewModel.stopScanning()
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
        scaffoldState = scaffoldState
    ) { padding ->

        if (showConnectionDialog.value){
            Dialog(
                onDismissRequest = { /*TODO*/ },
                DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
            ) {
                Box(
                    contentAlignment= Alignment.Center,
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color.White, shape = RoundedCornerShape(12.dp))
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.padding(6.dp, 0.dp, 0.dp, 0.dp))
                        Text(text = "Connecting...", Modifier.padding(0.dp, 8.dp, 0.dp, 0.dp))
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ){
            items(items = bleDevices){ device ->
                DeviceCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                                   viewModel.deviceSelect(device)
                        },
                    deviceMacAddress = device.MAC,
                    deviceName = device.name,
                    rssi = device.rssi
                )
            }
        }

    }
}