package com.dikoresearchsuspensioncontroller.feature_controller.presentation.controlscreen.components

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dikoresearchsuspensioncontroller.R

enum class PresetFloatingButtonState{
    COLLAPSED,
    EXPANDED
}

enum class PresetButtonState{
    COLLAPSED,
    EXPANDED
}

@Composable
fun PressureRegulationGroup(
    floatButtonSize: Dp = 56.dp,
    presetButtonSize: Dp = 56.dp,
    expandedState: MutableState<PresetFloatingButtonState>,
    presetButton1State: MutableState<PresetButtonState>,
    presetButton2State: MutableState<PresetButtonState>,
    presetButton3State: MutableState<PresetButtonState>,
    floatButtonClicked: () -> Unit,
    selectClicked: (Int) -> Unit,
    saveClicked: (Int) -> Unit,
    sendClicked: (Int) -> Unit,
){
    val floatingButtonTransition = updateTransition(targetState = expandedState.value, label = "floatingButtonTransition")

    val floatingButtonOffset = floatingButtonTransition.animateDp(
        label = "",
        transitionSpec = {
            tween(durationMillis = 300)
        }
    ) {state ->
        if (state == PresetFloatingButtonState.EXPANDED) (-80).dp else 0.dp
    }

    val presetButtonsOffset = floatingButtonTransition.animateDp(
        label = "",
        transitionSpec = {
            tween(durationMillis = 300)
        }
    ){state ->
        if (state == PresetFloatingButtonState.EXPANDED) (-80).dp else 0.dp
    }

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .height(presetButtonSize*2)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ){
        Box(){
            PressurePresetGroup(
                pressurePresetExpanded = presetButton1State,
                buttonSize = presetButtonSize,
                horizontalOffset = presetButtonsOffset.value,
                presetIcon = painterResource(id = R.drawable.ic_number_1),
                presetButtonClicked = {
                    presetButton2State.value = PresetButtonState.COLLAPSED
                    presetButton3State.value = PresetButtonState.COLLAPSED
                    selectClicked(1)
                },
                saveButtonClicked = {
                    saveClicked(1)
                },
                sendButtonClicked = {
                    sendClicked(1)
                }
            )
            PressurePresetGroup(
                pressurePresetExpanded = presetButton2State,
                buttonSize = presetButtonSize,
                horizontalOffset = presetButtonsOffset.value*2,
                presetIcon = painterResource(id = R.drawable.ic_number_2),
                presetButtonClicked = {
                    presetButton1State.value = PresetButtonState.COLLAPSED
                    presetButton3State.value = PresetButtonState.COLLAPSED
                    selectClicked(2)
                },
                saveButtonClicked = {
                    saveClicked(2)
                },
                sendButtonClicked = {
                    sendClicked(2)
                }
            )
            PressurePresetGroup(
                pressurePresetExpanded = presetButton3State,
                buttonSize = presetButtonSize,
                horizontalOffset = presetButtonsOffset.value*3,
                presetIcon = painterResource(id = R.drawable.ic_number_3),
                presetButtonClicked = {
                    presetButton2State.value = PresetButtonState.COLLAPSED
                    presetButton1State.value = PresetButtonState.COLLAPSED
                    selectClicked(3)
                },
                saveButtonClicked = {
                    saveClicked(3)
                },
                sendButtonClicked = {
                    sendClicked(3)
                }
            )

            OutlinedButton(
                modifier = Modifier
                    .size(floatButtonSize),
                onClick = {
                    if (expandedState.value == PresetFloatingButtonState.COLLAPSED){
                        floatButtonClicked()
                        expandedState.value = PresetFloatingButtonState.EXPANDED
                    }
                    else {
                        expandedState.value = PresetFloatingButtonState.COLLAPSED
                        presetButton1State.value = PresetButtonState.COLLAPSED
                        presetButton2State.value = PresetButtonState.COLLAPSED
                        presetButton3State.value = PresetButtonState.COLLAPSED
                    }
                },
                shape = CircleShape
            ) {
                Box(){
                    Icon(
                        painter = painterResource(id = R.drawable.ic_truck),
                        contentDescription = null,
                        modifier = Modifier.offset(floatingButtonOffset.value, 0.dp)
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_right),
                        contentDescription = null,
                        modifier = Modifier.offset(floatingButtonOffset.value + 80.dp, 0.dp)
                    )
                }
            }
        }
    }
}