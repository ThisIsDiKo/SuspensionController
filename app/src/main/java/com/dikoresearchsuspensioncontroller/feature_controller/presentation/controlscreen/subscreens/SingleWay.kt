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
import com.dikoresearchsuspensioncontroller.feature_controller.domain.model.controller_models.PressureRegulationParameters
import com.dikoresearchsuspensioncontroller.feature_controller.presentation.controlscreen.ControlScreenViewModel
import com.dikoresearchsuspensioncontroller.feature_controller.presentation.controlscreen.components.AirBag
import com.dikoresearchsuspensioncontroller.feature_controller.presentation.controlscreen.components.ControlGroup
import com.dikoresearchsuspensioncontroller.feature_controller.presentation.controlscreen.components.PressureRegulationGroup

@Composable
fun SingleWay(
    viewModel: ControlScreenViewModel,
    padding: PaddingValues,
    pressure1: String,
    pressureTank: String,
    showPressureInTank: Boolean,
    showControlGroup: Boolean,
    showRegulationGroup: Boolean,
    writeOutputs: (String) -> Unit
){
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(padding),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Column(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AirBag(
                text = pressure1,
                textStyle = TextStyle(
                    color = Color.Black,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                ),
                showPreset = viewModel.presetState.value.showPresetValues,
                presetText = viewModel.presetState.value.presetPressure1,
                backgroundColor = Color.LightGray,
                borderColor = Color.DarkGray
            )

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
            }
        }
        if (showRegulationGroup){
            PressureRegulationGroup(
                expandedState = viewModel.expandedState,
                presetButton1State = viewModel.presetButton1State,
                presetButton2State = viewModel.presetButton2State,
                presetButton3State = viewModel.presetButton3State,
                floatButtonClicked = {
                    //Stop regulation
                    viewModel.hidePresetValues()
                    viewModel.writeRegulationParams(
                        PressureRegulationParameters(
                            commandType = "STOP",
                            waysType = "waysType",
                            airPreparingType = "airPreparingType",
                            useTankPressure = false,
                            airPressureSensorType = "None",
                            refPressure1mV = 0,
                            refPressure2mV = 0,
                            refPressure3mV = 0,
                            refPressure4mV = 0
                        )
                    )
                },
                selectClicked = {
                    viewModel.selectPresetClicked(it)
                },
                saveClicked = {
                    viewModel.savePresetClicked(it)
                },
                sendClicked = {
                    viewModel.sendPresetClicked(it)
                }
            )
        }
    }
}