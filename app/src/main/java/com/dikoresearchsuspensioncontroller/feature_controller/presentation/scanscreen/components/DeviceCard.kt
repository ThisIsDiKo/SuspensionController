package com.dikoresearchsuspensioncontroller.feature_controller.presentation.scanscreen.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dikoresearchsuspensioncontroller.R


@Composable
fun DeviceCard(
    modifier: Modifier,
    deviceMacAddress: String,
    deviceName: String,
    rssi: String
){
    Card(
        modifier = modifier,
        elevation = 10.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_wifi_50dp),
                contentDescription = null,
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = deviceName)
                Text(text = deviceMacAddress)
            }

            Text(
                text = "Rssi: $rssi",

            )
        }
    }
}

@Preview
@Composable
fun DeviceCardPreview(){
    DeviceCard(
        modifier = Modifier.fillMaxWidth(),
        deviceMacAddress = "00:00:00:00:00:00",
        deviceName = "Unknown",
        rssi = "-123"
    )
}