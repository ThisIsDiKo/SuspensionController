package com.dikoresearchsuspensioncontroller.feature_controller.presentation.controlscreen.subscreens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dikoresearchsuspensioncontroller.feature_controller.presentation.controlscreen.components.AirBag
import com.dikoresearchsuspensioncontroller.feature_controller.presentation.controlscreen.components.ControlGroup

@Composable
fun SingleWay(
    padding: PaddingValues,
    pressure1: String,
    pressureTank: String,
    showPressureInTank: Boolean,
    writeOutputs: (String) -> Unit
){
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(padding),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {

                AirBag(
                    text = pressure1,
                    textStyle = TextStyle(
                        color = Color.Black,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    showPreset = true,
                    presetText = "10.0",
                    backgroundColor = Color.LightGray,
                    borderColor = Color.DarkGray
                )
            }

            if(showPressureInTank){
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Pressure in tank: $pressureTank",
                    style = TextStyle(
                        color = Color.DarkGray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ControlGroup(
                buttonWidth = 100.dp,
                spacerHeight = 15.dp,
                onUpPressed = {
                    writeOutputs("0001")
                },
                onDownPressed = {
                    writeOutputs("0010")
                },
                onUpDownReleased =  {
                    writeOutputs("0000")
                },
            )
        }
    }
}