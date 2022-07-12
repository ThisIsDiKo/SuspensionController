package com.dikoresearchsuspensioncontroller.feature_controller.presentation.controlscreen.components

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dikoresearchsuspensioncontroller.R
import timber.log.Timber

@Composable
fun PressurePresetGroup(
    pressurePresetExpanded: MutableState<PresetButtonState>,
    buttonSize: Dp,
    horizontalOffset: Dp,
    presetIcon: Painter,
    presetButtonClicked: () -> Unit,
    saveButtonClicked: () -> Unit,
    sendButtonClicked: () -> Unit
){
    val transition = updateTransition(targetState = pressurePresetExpanded.value, label = "")
    val verticalOffset = transition.animateDp(
        label = "",
        transitionSpec = {
            tween(durationMillis = 300)
        }
    ) {state ->
        if (state == PresetButtonState.EXPANDED){
            buttonSize/2 + 2.dp
        }
        else {
            0.dp
        }
    }

    Box(
        modifier = Modifier
            .offset(horizontalOffset, 0.dp)
    ){
        if (verticalOffset.value > 0.dp){
            OutlinedButton(
                modifier = Modifier
                    .offset(0.dp, verticalOffset.value)
                    .size(buttonSize),
                onClick = {
                    saveButtonClicked()
                },
                shape = CircleShape,
                ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_save_disk),
                    contentDescription = null
                )
            }
            OutlinedButton(
                modifier = Modifier
                    .offset(0.dp, -verticalOffset.value)
                    .size(buttonSize),
                onClick = {
                    sendButtonClicked()
                },
                shape = CircleShape,
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = null
                )
            }
        }
        else {
            OutlinedButton(
                modifier = Modifier
                    .offset(0.dp, 0.dp)
                    .size(buttonSize),
                onClick = {
                    if (pressurePresetExpanded.value == PresetButtonState.EXPANDED){
                        pressurePresetExpanded.value = PresetButtonState.COLLAPSED
                    }
                    else {
                        pressurePresetExpanded.value = PresetButtonState.EXPANDED
                    }
                    Timber.i("Clicked from preset button")
                    presetButtonClicked()
                    //Need to activate click button action to send to ble
                },
                shape = CircleShape
            ) {
                if (pressurePresetExpanded.value == PresetButtonState.EXPANDED){
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = null
                    )
                }
                else {
                    Icon(
                        painter = presetIcon,
                        contentDescription = null
                    )
                }

            }
        }


    }
}