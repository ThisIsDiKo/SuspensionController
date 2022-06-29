package com.dikoresearchsuspensioncontroller.feature_controller.presentation.controlscreen.subscreens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dikoresearchsuspensioncontroller.feature_controller.domain.model.controller_models.PressureRegulationParameters
import com.dikoresearchsuspensioncontroller.feature_controller.presentation.controlscreen.components.AirBag
import com.dikoresearchsuspensioncontroller.feature_controller.presentation.controlscreen.components.ControlGroup

@Composable
fun DoubleWay(
    padding: PaddingValues,
    pressure1: String,
    pressure2: String,
    pressureTank: String,
    showPressureInTank: Boolean,
    showControlGroup: Boolean,
    showRegulationGroup: Boolean,
    writeOutputs: (String) -> Unit,
    writeRegulation: (PressureRegulationParameters) -> Unit
){
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(padding),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
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
                    backgroundColor = Color.LightGray,
                    borderColor = Color.DarkGray
                )
                AirBag(
                    text = pressure2,
                    textStyle = TextStyle(
                        color = Color.Black,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    backgroundColor = Color.LightGray,
                    borderColor = Color.DarkGray
                )
            }

            if(showPressureInTank){
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Pressure in tank: $pressureTank")
            }
        }

        if (showControlGroup){
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
                ControlGroup(
                    buttonWidth = 115.dp,
                    spacerHeight = 15.dp,
                    onUpPressed = {
                        writeOutputs("0101")
                    },
                    onDownPressed = {
                        writeOutputs("1010")
                    },
                    onUpDownReleased =  {
                        writeOutputs("0000")
                    },
                )
                ControlGroup(
                    buttonWidth = 100.dp,
                    spacerHeight = 15.dp,
                    onUpPressed = {
                        writeOutputs("0100")
                    },
                    onDownPressed = {
                        writeOutputs("1000")
                    },
                    onUpDownReleased =  {
                        writeOutputs("0000")
                    },
                )
            }
        }

        if (showRegulationGroup){
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        val params = PressureRegulationParameters(
                            commandType = "START",
                            waysType = "DOUBLE",
                            airPreparingType = "RECEIVER",
                            airPressureSensorType = "None",
                            useTankPressure = false,
                            refPressure1mV = 1000,
                            refPressure2mV = 1000,
                            refPressure3mV = 1100,
                            refPressure4mV = 1300,
                        )
                        writeRegulation(params)
                    }
                ) {
                    Text(text = "Go preset 1")
                }
                Button(
                    onClick = {
                        val params = PressureRegulationParameters(
                            commandType = "START",
                            waysType = "DOUBLE",
                            airPreparingType = "RECEIVER",
                            airPressureSensorType = "None",
                            useTankPressure = false,
                            refPressure1mV = 500,
                            refPressure2mV = 500,
                            refPressure3mV = 555,
                            refPressure4mV = 565,
                        )
                        writeRegulation(params)
                    }
                ){
                    Text(text = "Go preset 2")
                }
                Button(
                    onClick = {
                        val params = PressureRegulationParameters(
                            commandType = "STOP",
                            waysType = "DOUBLE",
                            airPreparingType = "RECEIVER",
                            airPressureSensorType = "None",
                            useTankPressure = false,
                            refPressure1mV = 1000,
                            refPressure2mV = 1000,
                            refPressure3mV = 1100,
                            refPressure4mV = 1300,
                        )
                        writeRegulation(params)
                    }
                ){
                    Text(text = "stop")
                }
            }
        }
    }
}