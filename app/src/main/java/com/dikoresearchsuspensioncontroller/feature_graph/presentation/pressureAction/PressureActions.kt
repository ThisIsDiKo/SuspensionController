package com.dikoresearchsuspensioncontroller.feature_graph.presentation.pressureAction

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.dikoresearchsuspensioncontroller.R
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

enum class StoredPressuresState{
    COLLAPSED,
    EXPANDED
}

enum class SubButtonState{
    COLLAPSED,
    EXPANDED
}
@Composable
fun PressureAction(){
    val expandedState = remember { mutableStateOf(StoredPressuresState.COLLAPSED)}
    val subButtonState = remember { mutableStateOf(SubButtonState.COLLAPSED)}
    val visible = remember { mutableStateOf(false) }

    val transition = updateTransition(targetState = expandedState.value, label = "")
    val subButtonTransition = updateTransition(targetState = subButtonState.value, label = "")

    val rotation = transition.animateFloat(
        label = "",
        transitionSpec = {
            tween(durationMillis = 100)
        }
    ) { state ->
        if (state == StoredPressuresState.EXPANDED) 45f else 0f
    }

    val translate = transition.animateDp(
        label = "",
        transitionSpec = {
            tween(durationMillis = 300)
        }
    ) { state ->
        if (state == StoredPressuresState.EXPANDED) (-80).dp else 0.dp
    }

    val translateButtons = transition.animateDp(label = "",
        transitionSpec = {
            tween(durationMillis = 300)
        }
    ) { state ->
        if (state == StoredPressuresState.EXPANDED) (-80).dp else 0.dp
    }

    val translateSubButtons = subButtonTransition.animateDp(label = "",
        transitionSpec = {
            tween(durationMillis = 300)
        }
    ) { state ->
        if (state == SubButtonState.EXPANDED) (-64).dp else 0.dp
    }

    val counter = remember { mutableStateOf(0)}

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ){
        Box(){
            Button(
                modifier = Modifier
                    .offset(translateButtons.value, 0.dp)
                    .size(56.dp)
                    .longClick(
                        interactionSource = remember { MutableInteractionSource() },
                        enabled = true,
                        onClick = {
                            counter.value++
                            if (counter.value == 100) {
                                Timber.e("Long press clicked")
                            }
                        },
                        onRelease = {
                            if (counter.value < 100) {
                                Timber.e("Simple click")
                            }
                            counter.value = 0
                        }
                    ),
                //enabled = expandedState.value == StoredPressuresState.EXPANDED,
                onClick = {},
                shape = CircleShape
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_number_3),
                    contentDescription = null
                )
            }

            Button(
                modifier = Modifier
                    .offset(translateButtons.value * 2, 0.dp)
                    .size(56.dp),
                onClick = { /*TODO*/ },
                shape = CircleShape
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_number_2),
                    contentDescription = null
                )
            }

            Box(
                contentAlignment = Alignment.Center
            ){
                Button(
                    modifier = Modifier
                        .offset(translateButtons.value * 3, translateSubButtons.value)
                        .size(56.dp),
                    onClick = { /*TODO*/ },
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = null
                    )
                }
                Button(
                    modifier = Modifier
                        .offset(translateButtons.value * 3, -translateSubButtons.value)
                        .size(56.dp),
                    onClick = { /*TODO*/ },
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = null,
                    )
                }
//                OutlinedButton(onClick = { },
//                    modifier = Modifier.offset(translateButtons.value * 3, -translateSubButtons.value)
//                        .size(40.dp),
//                    shape = CircleShape,
//                    border= BorderStroke(2.dp, Color(0XFF0F9D58)),
//                    colors = ButtonDefaults.outlinedButtonColors(contentColor =  Color.Blue)
//                ) {
//                    // Adding an Icon "Add" inside the Button
//                    Icon(Icons.Default.Add ,contentDescription = "content description", tint=Color(0XFF0F9D58))
//                }
                Button(
                    modifier = Modifier
                        .offset(translateButtons.value * 3, 0.dp)
                        .size(56.dp),
                    onClick = {
                        if (subButtonState.value == SubButtonState.EXPANDED){
                            subButtonState.value = SubButtonState.COLLAPSED
                        }
                        else {
                            subButtonState.value = SubButtonState.EXPANDED
                        }
                    },
                    shape = CircleShape
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_number_1),
                        contentDescription = null
                    )
                }
            }


            OutlinedButton(
                modifier = Modifier.size(56.dp),
                onClick = {
                    visible.value = !visible.value
                    if (expandedState.value == StoredPressuresState.COLLAPSED){
                        expandedState.value = StoredPressuresState.EXPANDED
                    }
                    else {
                        expandedState.value = StoredPressuresState.COLLAPSED
                        subButtonState.value = SubButtonState.COLLAPSED
                    }
                },
                shape = CircleShape
            ) {
                Box() {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_truck),
                        contentDescription = null,
                        modifier = Modifier
                            //.rotate(rotation.value)
                            .offset(translate.value, 0.dp)
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_right),
                        contentDescription = null,
                        modifier = Modifier
                            //.rotate(rotation.value)
                            .offset(translate.value + 80.dp, 0.dp)
                    )
                }
            }
        }
        CircularProgressIndicator(progress = if (counter.value < 20) 0f else (counter.value - 20f) / 100f)
    }
}

fun Modifier.longClick(
    interactionSource: MutableInteractionSource,
    enabled: Boolean,
    delayMillis: Long = 20,
    onClick: () -> Unit,
    onRelease: () -> Unit
):Modifier = composed {
    val currentClickListener by rememberUpdatedState(onClick)

    pointerInput(interactionSource, enabled) {
        forEachGesture {
            coroutineScope {
                awaitPointerEventScope {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val downPress = PressInteraction.Press(down.position)

                    val heldButtonJob = launch {
                        interactionSource.emit(downPress)
                        var currentDelayMillis = delayMillis
                        while(enabled && down.pressed){
                            currentClickListener()
                            delay(currentDelayMillis)
                        }
                    }
                    val up = waitForUpOrCancellation()
                    heldButtonJob.cancel()

                    val releaseOrCancel = when (up) {
                        null -> PressInteraction.Cancel(downPress)
                        else -> PressInteraction.Release(downPress)
                    }
                    launch {
                        // Send the result through the interaction source
                        interactionSource.emit(releaseOrCancel)
                        onRelease()
                    }
                }
            }
        }
    }.indication(interactionSource, rememberRipple())
}