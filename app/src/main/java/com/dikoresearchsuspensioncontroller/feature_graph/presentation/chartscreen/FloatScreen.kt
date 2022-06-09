package com.dikoresearchsuspensioncontroller.feature_graph.presentation.chartscreen

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

enum class MultiFabState{
    COLLAPSED,
    EXPANDED
}

class MultiFabItem(
    val identifier: String,
    val icon: ImageBitmap,
    val label: String
)

@Composable
fun FloatScreen(){
    val scaffoldState = rememberScaffoldState()
    val toState = remember{ mutableStateOf(MultiFabState.COLLAPSED) }

    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            MultiFloatingActionButton(
                fabIcon = Icons.Filled.Search,
                toState = toState.value,
                stateChanged = {state ->
                    toState.value = state
                }
            )
        }
    ) { paddingValues ->
        val alpha = if (toState.value == MultiFabState.EXPANDED) 0.4f else 0f
        Box(
            modifier = Modifier
                //.alpha(animateAsState(alpha).value)
                .background(Color.LightGray)
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}

@Composable
fun MultiFloatingActionButton(
    fabIcon: ImageVector,
    toState: MultiFabState,
    stateChanged: (fabState: MultiFabState) -> Unit
){
    val transition = updateTransition(targetState = toState, label = "")

    val scale: Float by transition.animateFloat(label = "") {state ->
        if (state == MultiFabState.EXPANDED) 56f else 0f
    }

    val alpha: Float by transition.animateFloat(label = "",
        transitionSpec = {
            tween(durationMillis = 50)
        }
    ) { state ->
        if (state == MultiFabState.EXPANDED) 1f else 0f
    }

    val rotation: Float by transition.animateFloat(label = "") { state ->
        if (state == MultiFabState.EXPANDED) 45f else 0f
    }

    Column(horizontalAlignment = Alignment.End) {
        FloatingActionButton(
            onClick = {
                stateChanged(
                    if (transition.currentState == MultiFabState.EXPANDED){
                        MultiFabState.COLLAPSED
                    }
                    else {
                        MultiFabState.EXPANDED
                    }
                )
            }
        ) {
            Icon(
                imageVector = fabIcon,
                contentDescription = null,
                modifier = Modifier.rotate(rotation)
            )
        }
    }
}

@Composable
private fun MiniFabItem(
    fabIcon: ImageBitmap,
    item: MultiFabItem,
    buttonScale: Float,
    onFabItemClicked: (item: MultiFabItem) -> Unit
){
    val buttonColor = MaterialTheme.colors.secondary
    Canvas(
        modifier = Modifier
            .size(32.dp)
            .clickable(
                onClick = { onFabItemClicked(item) }
            )
    ){
        drawCircle(color = buttonColor, buttonScale)
        drawImage(
            item.icon,
            topLeft = Offset(
                (this.center.x) - (item.icon.width / 2),
                (this.center.y) - (item.icon.width / 2)
            )
        )
    }
}