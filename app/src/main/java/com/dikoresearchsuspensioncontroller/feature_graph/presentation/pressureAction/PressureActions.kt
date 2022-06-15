package com.dikoresearchsuspensioncontroller.feature_graph.presentation.pressureAction

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import timber.log.Timber

enum class StoredPressuresState{
    COLLAPSED,
    EXPANDED
}
@Composable
fun PressureAction(){
    val expandedState = remember { mutableStateOf(StoredPressuresState.COLLAPSED)}
    val visible = remember { mutableStateOf(false) }
    val transition = updateTransition(targetState = expandedState.value, label = "")
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



    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ){
        Box(){
            Button(
                modifier = Modifier.offset(translateButtons.value, 0.dp),
                //enabled = expandedState.value == StoredPressuresState.EXPANDED,
                onClick = { Timber.e("First button clicked")},
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = null
                )
            }

            Button(
                modifier = Modifier.offset(translateButtons.value * 2, 0.dp),
                onClick = { /*TODO*/ },
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBox,
                    contentDescription = null
                )
            }

            Button(
                modifier = Modifier.offset(translateButtons.value * 3, 0.dp),
                onClick = { /*TODO*/ },
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null
                )
            }

            Button(
                onClick = {
                    visible.value = !visible.value
                    if (expandedState.value == StoredPressuresState.COLLAPSED){
                        expandedState.value = StoredPressuresState.EXPANDED
                    }
                    else {
                        expandedState.value = StoredPressuresState.COLLAPSED
                    }
                },
                shape = CircleShape
            ) {
                Box() {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier
                            //.rotate(rotation.value)
                            .offset(translate.value, 0.dp)
                    )
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        modifier = Modifier
                            //.rotate(rotation.value)
                            .offset(translate.value + 80.dp, 0.dp)
                    )
                }
            }
        }
    }
}
