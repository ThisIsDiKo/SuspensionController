package com.dikoresearchsuspensioncontroller.feature_controller.presentation.controlscreen


import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dikoresearchsuspensioncontroller.R
import com.dikoresearchsuspensioncontroller.feature_controller.domain.model.DeviceMode
import com.dikoresearchsuspensioncontroller.feature_controller.presentation.controlscreen.components.AirBag
import com.dikoresearchsuspensioncontroller.feature_controller.presentation.controlscreen.components.ControlGroup
import com.dikoresearchsuspensioncontroller.feature_controller.presentation.scanscreen.subscreens.DoubleWay
import com.dikoresearchsuspensioncontroller.feature_controller.presentation.scanscreen.subscreens.SingleWay
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ControlScreen(
    navController: NavController,
    viewModel: ControlScreenViewModel = hiltViewModel()
){
    val scaffoldState = rememberScaffoldState()
    val controllerData = viewModel.sensorsDataState.value
    val lifecycleOwner = LocalLifecycleOwner.current

    val showReconnectionDialog = viewModel.showReconnectionDialog

    val showPressureInTank = viewModel.showPressureInTank
    val deviceMode = viewModel.deviceMode

    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    )

    LaunchedEffect(key1 = true){
        viewModel.eventFlow.collectLatest { event ->
            when(event){
                is UiEventControlScreen.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message
                    )
                }
                is UiEventControlScreen.NavigateTo -> {
                    navController.navigate(event.destination){
                        popUpTo("controlscreen"){
                            inclusive = true
                        }
                    }
                }
                is UiEventControlScreen.StartReadingSensors -> {
                    viewModel.startReadingSensorsValues()
                }
            }
        }
    }

    DisposableEffect(
        key1 = lifecycleOwner,
        effect = {
            val observer = LifecycleEventObserver{ _, event ->
                if (event == Lifecycle.Event.ON_RESUME){
                    if (permissionState.allPermissionsGranted){
                        viewModel.setScreenActive(true)
                        viewModel.setConnectionStateObserver()
                        viewModel.startReadingSensorsValues()
                    }
                    else{
                        viewModel.stopReadingSensorsValues()
                        viewModel.clearConnectionStateObserver()
                        navController.navigate("startscreen")
                    }

                }
                else if (event == Lifecycle.Event.ON_PAUSE){
                    viewModel.setScreenActive(false)
                    viewModel.writeOutputs("0000")
                    //viewModel.clearConnectionStateObserver()
                    viewModel.stopReadingSensorsValues()
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    )

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {

                },
                actions = {
//                    IconButton(onClick = {
//                        Timber.i("Go to graph clicked")
//                    }) {
//                        Icon(
//                            painter = painterResource(id = R.drawable.ic_baseline_show_chart_36),
//                            contentDescription = "")
//                    }
//                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = {
                        navController.navigate("settingsscreen")
                    }) {
                        Icon(Icons.Filled.Settings, contentDescription = "")
                    }
                },
                backgroundColor = Color.White
            )
        },
    ) { padding ->
        if (showReconnectionDialog.value){
            Dialog(
                onDismissRequest = { /*TODO*/ },
                DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
            ) {
                Card(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(12.dp)
                ){
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.padding(8.dp))
                        Text(text = "Reconnecting...",modifier = Modifier.padding(8.dp))
                    }
                }
            }
        }
        when(deviceMode.value){
            DeviceMode.DoubleWay().alias -> {
                DoubleWay(
                    padding = padding,
                    pressure1 = controllerData.pressure1,
                    pressure2 = controllerData.pressure2,
                    pressureTank = controllerData.pressureTank,
                    showPressureInTank = showPressureInTank.value,
                    writeOutputs = viewModel::writeOutputs
                )
            }
            DeviceMode.SingleWay().alias -> {
                SingleWay(
                    padding = padding,
                    pressure1 = controllerData.pressure1,
                    pressureTank = controllerData.pressureTank,
                    showPressureInTank = showPressureInTank.value,
                    writeOutputs = viewModel::writeOutputs
                )
            }
            else -> {
                Text(text = "Hello :-)")
            }
        }

    }
}
