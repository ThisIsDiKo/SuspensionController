package com.dikoresearchsuspensioncontroller.feature_controller.presentation.controlscreen.components


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AirBag(
    modifier: Modifier = Modifier,
    text: String,
    textStyle: TextStyle,
    showPreset: Boolean,
    presetText: String,
    backgroundColor: Color,
    borderColor: Color
){
//    Text(
//        text = text,
//        style = textStyle,
//        textAlign = TextAlign.Center,
//        modifier = modifier
//            .wrapContentSize(Alignment.Center)
//            .graphicsLayer {
//                shadowElevation = 8.dp.toPx()
//                shape = AirBagShape()
//                clip = true
//            }
//            .background(color = backgroundColor)
//            .border(
//                width = 2.dp,
//                color = borderColor,
//                shape = AirBagShape()
//            )
//            .padding(start = 40.dp, top = 32.dp, end = 40.dp, bottom = 32.dp)
//    )

    Box(
        modifier = modifier
            .height(100.dp)
            .graphicsLayer {
                shadowElevation = 8.dp.toPx()
                shape = AirBagShape()
                clip = true
            }
            .background(color = backgroundColor)
            .border(
                width = 2.dp,
                color = borderColor,
                shape = AirBagShape()
            )
            .padding(start = 40.dp, top = 8.dp, end = 40.dp, bottom = 8.dp)
    ){
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = text,
                style = textStyle,
                textAlign = TextAlign.Center,
            )
            if (showPreset){
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = presetText,
                    style = textStyle.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Light,
                    ),
                    textAlign = TextAlign.Center,
                )
            }

        }
    }

}

class AirBagShape: Shape{
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(
            path = drawAirbagPath(size = size)
        )
    }
}

fun drawAirbagPath(size: Size): Path {
    val radius = size.height / 4.0f

    return Path().apply {
        reset()

        arcTo(
            rect = Rect(
                left = -radius,
                top = -radius,
                right = radius,
                bottom = radius
            ).translate(Offset(radius, radius)),
            startAngleDegrees = 90.0f,
            sweepAngleDegrees = 180.0f,
            forceMoveTo = false
        )
        lineTo(x = size.width-radius, y = 0f)

        arcTo(
            rect = Rect(
                left = size.width-2f*radius,
                top = 0f,
                right = size.width,
                bottom = 2f*radius
            ),
            startAngleDegrees = -90.0f,
            sweepAngleDegrees = 180.0f,
            forceMoveTo = false
        )
        lineTo(x = size.width-radius, y = 2f * radius)
        arcTo(
            rect = Rect(
                left = size.width-2f*radius,
                top = 2f*radius,
                right = size.width,
                bottom = size.height
            ),
            startAngleDegrees = -90.0f,
            sweepAngleDegrees = 180.0f,
            forceMoveTo = false
        )
        lineTo(x = radius, y = size.height)

        arcTo(
            rect = Rect(
                left = 0f,
                top = 2f*radius,
                right = 2f*radius,
                bottom = size.height
            ),
            startAngleDegrees = 90.0f,
            sweepAngleDegrees = 180.0f,
            forceMoveTo = false
        )
        lineTo(x = radius, y = 2f * radius)

        close()
    }
}

@Preview
@Composable
fun AirBagPreview(){
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AirBag(
            text = "10.0",
            textStyle = TextStyle(
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            ),
            showPreset = true,
            presetText = "10.0",
            backgroundColor = Color.LightGray,
            borderColor = Color.DarkGray
        )

        AirBag(
            text = "10.0",
            textStyle = TextStyle(
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            ),
            showPreset = false,
            presetText = "10.0",
            backgroundColor = Color.LightGray,
            borderColor = Color.DarkGray
        )
    }

}