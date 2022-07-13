package com.dikoresearchsuspensioncontroller.feature_graph.presentation.pressureAction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.dikoresearchsuspensioncontroller.feature_controller.presentation.controlscreen.components.PresetButtonState
import com.dikoresearchsuspensioncontroller.feature_controller.presentation.controlscreen.components.PresetFloatingButtonState
import com.dikoresearchsuspensioncontroller.feature_controller.presentation.controlscreen.components.PressureRegulationGroup

@Preview(device = Devices.PIXEL_4)
@Composable
fun PressureActionsScreen(){
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        //PressureAction()
        PressureRegulationGroup(
            expandedState = remember {
                mutableStateOf(PresetFloatingButtonState.COLLAPSED)
            },
            presetButton1State = remember {
                mutableStateOf(PresetButtonState.COLLAPSED)
            },
            presetButton2State = remember {
                mutableStateOf(PresetButtonState.COLLAPSED)
            },
            presetButton3State = remember {
                mutableStateOf(PresetButtonState.COLLAPSED)
            },
            floatButtonClicked = {

            },
            selectClicked = {

            },
            saveClicked = {

            },
            sendClicked = {

            }
        )
    }
}