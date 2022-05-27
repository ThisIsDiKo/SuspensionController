package com.dikoresearchsuspensioncontroller.feature_controller.presentation.controlscreen


import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dikoresearchsuspensioncontroller.feature_controller.presentation.controlscreen.components.AirBag
import com.dikoresearchsuspensioncontroller.feature_controller.presentation.controlscreen.components.ControlGroup
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ControlScreen(
    navController: NavController,
    viewModel: ControlScreenViewModel = hiltViewModel()
){
    val scaffoldState = rememberScaffoldState()
    val controllerData = viewModel.sensorsDataState.value
    val lifecycleOwner = LocalLifecycleOwner.current

    val showReconnectionDialog = viewModel.showReconnectionDialog

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
            }
        }
    }

    DisposableEffect(
        key1 = lifecycleOwner,
        effect = {
            val observer = LifecycleEventObserver{ _, event ->
                if (event == Lifecycle.Event.ON_RESUME){
                    viewModel.startReadingSensorsValues()
                }
                else if (event == Lifecycle.Event.ON_PAUSE){
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
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (showReconnectionDialog.value){
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "Reconnecting")
                }
                Spacer(Modifier.height(10.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AirBag(
                    text = controllerData.pressure1,
                    textStyle = TextStyle(
                        color = Color.Black,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    backgroundColor = Color.LightGray,
                    borderColor = Color.DarkGray
                )
                AirBag(
                    text = controllerData.pressure2,
                    textStyle = TextStyle(
                        color = Color.Black,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    backgroundColor = Color.LightGray,
                    borderColor = Color.DarkGray
                )
            }
            Spacer(modifier = Modifier.height(30.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ControlGroup(
                    buttonWidth = 100.dp,
                    spacerHeight = 15.dp,
                    onUpPressed = {
                        viewModel.writeOutputs("0001")
                    },
                    onDownPressed = {
                        viewModel.writeOutputs("0010")
                    },
                    onUpDownReleased =  {
                        viewModel.writeOutputs("0000")
                    },
                )
                ControlGroup(
                    buttonWidth = 115.dp,
                    spacerHeight = 15.dp,
                    onUpPressed = {
                        viewModel.writeOutputs("0101")
                    },
                    onDownPressed = {
                        viewModel.writeOutputs("1010")
                    },
                    onUpDownReleased =  {
                        viewModel.writeOutputs("0000")
                    },
                )
                ControlGroup(
                    buttonWidth = 100.dp,
                    spacerHeight = 15.dp,
                    onUpPressed = {
                        viewModel.writeOutputs("0100")
                    },
                    onDownPressed = {
                        viewModel.writeOutputs("1000")
                    },
                    onUpDownReleased =  {
                        viewModel.writeOutputs("0000")
                    },
                )
            }
        }
    }
}
