package com.dikoresearchsuspensioncontroller.feature_controller.presentation.settingsscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dikoresearchsuspensioncontroller.feature_controller.domain.model.*
import com.dikoresearchsuspensioncontroller.feature_controller.presentation.scanscreen.UiEventScanScreen
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsScreenViewModel = hiltViewModel()
){
    val scaffoldState = rememberScaffoldState()
    
    val settings = viewModel.applicationSettingsFlow.collectAsState(
        initial = ApplicationSettings(
            deviceName = "Unknown",
            deviceAddress = "--------",
            deviceFirmwareVersion = "---",
            deviceMode = DeviceMode.DoubleWay(),
            deviceType = DeviceType.SimplePressure(),
            useTankPressure = false,
            pressureSensorType = PressureSensor.China_0_20(),
            pressureUnits = PressureUnits.Bar()
        )
    )

    LaunchedEffect(key1 = true){
        viewModel.eventFlow.collectLatest { event ->
            when(event){
                is UiEventSettingsScreen.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message
                    )
                }
                is UiEventSettingsScreen.NavigateTo -> {
                    navController.navigate(event.destination){
                        popUpTo("settingsscreen"){
                            inclusive = true
                        }
                    }
                }
                is UiEventSettingsScreen.NavigateUp -> {
                    navController.navigateUp()
                }
            }
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Settings")
                },
                navigationIcon = {
                    if (navController.previousBackStackEntry != null){
                        IconButton(
                            onClick = {
                                navController.navigateUp()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = null
                            )
                        }
                    }
                },
                backgroundColor = Color.White
            )
        },
    ) { padding ->

        if (viewModel.showClearDialog.value){
            Dialog(onDismissRequest = {
                viewModel.dialogDismissRequest()
            }) {
                Card(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = "Clear device info",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Delete device info and go to scan screen",
                            fontSize = 12.sp,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End
                        ){
                            TextButton(onClick = {viewModel.dialogDismissRequest()}) {
                                Text(text = "Cancel")
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            TextButton(onClick = {viewModel.clearDeviceInfo(settings.value)}) {
                                Text(
                                    text = "Clear",
                                    color = Color.Gray
                                )
                            }
//                            Button(onClick = {viewModel.dialogDismissRequest()}) {
//                                Text(text = "Cancel")
//                            }
//                            Spacer(modifier = Modifier.width(10.dp))
//                            Button(
//                                onClick = {viewModel.clearDeviceInfo(settings.value)},
//                                modifier = Modifier.background(Color.White)
//                            ) {
//                                Text(text = "Clear")
//                            }
                        }
                    }
                }
            }
        }

        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Device Name"
                )
                Text(
                    text = settings.value.deviceName,
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Device Address"
                )
                Text(
                    text = settings.value.deviceAddress,
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Device Type"
                )
                Text(
                    text = settings.value.deviceType.alias
                )
            }
            Divider(startIndent = 6.dp, thickness = 1.dp, color = Color.Black)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Show tank pressure"
                )

                Switch(
                    checked = settings.value.useTankPressure,
                    onCheckedChange = {
                        viewModel.setUseTankPressure(it)
                    }
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val expanded = remember { mutableStateOf(false)}
                val items = listOf(DeviceMode.SingleWay(), DeviceMode.DoubleWay())
                Text(
                    text = "Device Mode"
                )
                Box(
                    modifier = Modifier.wrapContentSize(Alignment.TopStart)
                ){
                    Text(
                        text = settings.value.deviceMode.alias,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { expanded.value = true }
                    )
                    DropdownMenu(
                        expanded = expanded.value,
                        onDismissRequest = {
                            expanded.value = false
                        }
                    ) {
                        items.forEachIndexed { index, deviceMode ->
                            DropdownMenuItem(
                                onClick = {
                                    viewModel.setDeviceMode(deviceMode)
                                    expanded.value = false
                                }
                            ) {
                                Text(text = deviceMode.alias)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val expanded = remember { mutableStateOf(false)}
                val items = listOf(PressureSensor.China_0_20(), PressureSensor.Catterpillar())
                Text(
                    text = "Pressure Sensor"
                )
                Box(
                    modifier = Modifier.wrapContentSize(Alignment.TopStart)
                ){
                    Text(
                        text = settings.value.pressureSensorType.alias,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { expanded.value = true }
                    )
                    DropdownMenu(
                        expanded = expanded.value,
                        onDismissRequest = {
                            expanded.value = false
                        }
                    ) {
                        items.forEachIndexed { index, pressureSensor ->
                            DropdownMenuItem(
                                onClick = {
                                    viewModel.setPressureSensor(pressureSensor)
                                    expanded.value = false
                                }
                            ) {
                                Text(text = pressureSensor.alias)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val expanded = remember { mutableStateOf(false)}
                val items = listOf(PressureUnits.Bar(), PressureUnits.Psi())
                Text(
                    text = "Pressure Units"
                )
                Box(
                    modifier = Modifier.wrapContentSize(Alignment.TopStart)
                ){
                    Text(
                        text = settings.value.pressureUnits.alias,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { expanded.value = true }
                    )
                    DropdownMenu(
                        expanded = expanded.value,
                        onDismissRequest = {
                            expanded.value = false
                        }
                    ) {
                        items.forEachIndexed { index, pressureUnits ->
                            DropdownMenuItem(
                                onClick = {
                                    viewModel.setPressureUnits(pressureUnits)
                                    expanded.value = false
                                }
                            ) {
                                Text(text = pressureUnits.alias)
                            }
                        }
                    }
                }
            }


            Divider(startIndent = 6.dp, thickness = 1.dp, color = Color.Black)
            Button(
                onClick = {
                    viewModel.showClearDialog()
                }
            ) {
                Text(text = "Clear Device info")
            }
        }
    }
}

@Preview
@Composable
fun SettingsScreenPreview(){
    SettingsScreen(navController = rememberNavController())
}