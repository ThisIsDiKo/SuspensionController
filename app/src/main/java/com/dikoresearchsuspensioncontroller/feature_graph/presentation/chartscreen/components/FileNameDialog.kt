package com.dikoresearchsuspensioncontroller.feature_graph.presentation.chartscreen.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun FileNameDialog(
    enabled: Boolean,
    onDismissRequest: () -> Unit,
    onAccept: (String) -> Unit
){

    if (enabled){
        val text = remember{
            val curDate = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd HH_mm_ss")
            val formatted = curDate.format(formatter)

            mutableStateOf("snapshot $formatted")
        }
        AlertDialog(
            onDismissRequest = {
                onDismissRequest()
            },
            title = {
                Text("Enter File Name")
            },
            text = {
                TextField(
                    value = text.value,
                    onValueChange = {
                        text.value = it
                    }
                )
            },
            buttons = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            onDismissRequest()
                        }
                    ){
                        Text(text = "Cancel")
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            val fileName = text.value.ifEmpty { "Unnamed" }
                            onAccept(fileName)
                        }
                    ){
                        Text(text = "Ok")
                    }
                }
            }
        )
    }
}