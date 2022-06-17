package com.dikoresearchsuspensioncontroller.feature_graph.presentation.chartscreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color

@Composable
fun SourceSelectionBlock(
    showVoltageAlias: String,
    showSource1Alias: String,
    showSource2Alias: String,
    showSource3Alias: String,
    showSource4Alias: String,
    showSource5Alias: String,

    showVoltage: Boolean,
    showSource1: Boolean,
    showSource2: Boolean,
    showSource3: Boolean,
    showSource4: Boolean,
    showSource5: Boolean,

    showSource1Color: Color,
    showSource2Color: Color,
    showSource3Color: Color,
    showSource4Color: Color,
    showSource5Color: Color,

    onShowVoltageChanged: (Boolean) -> Unit,
    onShowSource1Changed: (Boolean) -> Unit,
    onShowSource2Changed: (Boolean) -> Unit,
    onShowSource3Changed: (Boolean) -> Unit,
    onShowSource4Changed: (Boolean) -> Unit,
    onShowSource5Changed: (Boolean) -> Unit,
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ){

        Checkbox(
            checked = showVoltage,
            onCheckedChange = {
                onShowVoltageChanged(it)
            }
        )
        Text(showVoltageAlias)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ){
        Checkbox(
            checked = showSource1,
            onCheckedChange = {
                onShowSource1Changed(it)
            },
            colors = CheckboxDefaults.colors(checkedColor = showSource1Color)
        )
        Text(showSource1Alias)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ){
        Checkbox(
            checked = showSource2,
            onCheckedChange = {
                onShowSource2Changed(it)
            },
            colors = CheckboxDefaults.colors(checkedColor = showSource2Color)
        )
        Text(showSource2Alias)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ){
        Checkbox(
            checked = showSource3,
            onCheckedChange = {
                onShowSource3Changed(it)
            },
            colors = CheckboxDefaults.colors(checkedColor = showSource3Color)
        )
        Text(showSource3Alias)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ){
        Checkbox(
            checked = showSource4,
            onCheckedChange = {
                onShowSource4Changed(it)
            },
            colors = CheckboxDefaults.colors(checkedColor = showSource4Color)
        )
        Text(showSource4Alias)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ){
        Checkbox(
            checked = showSource5,
            onCheckedChange = {
                onShowSource5Changed(it)
            },
            colors = CheckboxDefaults.colors(checkedColor = showSource5Color)
        )
        Text(showSource5Alias)
    }
}