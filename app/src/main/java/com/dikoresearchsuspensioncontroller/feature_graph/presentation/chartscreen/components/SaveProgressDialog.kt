package com.dikoresearchsuspensioncontroller.feature_graph.presentation.chartscreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun SaveProgressDialog(
    enabled: Boolean
){
    if (enabled){
        Dialog(onDismissRequest = { /*TODO*/ }) {
            Column(
                modifier = Modifier.
                        padding(10.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Text("Saving file")
            }
        }
    }
}