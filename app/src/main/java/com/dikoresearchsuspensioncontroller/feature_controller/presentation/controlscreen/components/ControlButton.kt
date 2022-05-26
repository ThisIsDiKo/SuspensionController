package com.dikoresearchsuspensioncontroller.feature_controller.presentation.controlscreen.components

import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun ControlButton(
    modifier: Modifier,
    icon: ImageVector,
    onPress: () -> Unit,
    onRelease: () -> Unit
){
    val interactionSource = remember { MutableInteractionSource() }

    Box(modifier = modifier
        .actionWithRippleEffect(
            interactionSource = interactionSource,
            onPress = onPress,
            onRelease = onRelease
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription ="",
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp)
        )
    }
}

fun Modifier.actionWithRippleEffect(
    interactionSource: MutableInteractionSource,
    onPress: () -> Unit,
    onRelease: () -> Unit
): Modifier = composed {
    pointerInput(interactionSource){ //add enabled
        forEachGesture {
            coroutineScope {
                awaitPointerEventScope {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val pressed = PressInteraction.Press(down.position)
                    val job = launch {
                        interactionSource.emit(pressed)
                        onPress()
                    }

                    val up = waitForUpOrCancellation()
                    job.cancel()

                    val releaseOrCancel = when(up){
                        null -> PressInteraction.Cancel(pressed)
                        else -> PressInteraction.Release(pressed)
                    }

                    launch {
                        interactionSource.emit(releaseOrCancel)
                        onRelease()
                    }
                }
            }
        }
    }.indication(interactionSource, rememberRipple())
}