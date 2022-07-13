package com.dikoresearchsuspensioncontroller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dikoresearchsuspensioncontroller.feature_controller.presentation.controlscreen.ControlScreen
import com.dikoresearchsuspensioncontroller.feature_controller.presentation.scanscreen.ScanScreen
import com.dikoresearchsuspensioncontroller.feature_controller.presentation.settingsscreen.SettingsScreen
import com.dikoresearchsuspensioncontroller.feature_controller.presentation.startscreen.StartScreen
import com.dikoresearchsuspensioncontroller.feature_graph.presentation.chartscreen.*
import com.dikoresearchsuspensioncontroller.feature_graph.presentation.pressureAction.PressureActionsScreen
import com.dikoresearchsuspensioncontroller.ui.theme.SuspensionControllerTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import kotlin.random.Random

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            SuspensionControllerTheme {
                val systemUiController = rememberSystemUiController()
                val useDarkIcons = MaterialTheme.colors.isLight

                SideEffect {
                    systemUiController.setSystemBarsColor(
                        color = Color.Transparent,
                        darkIcons = useDarkIcons
                    )
                }

                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "startscreen"){
                    composable("startscreen"){ StartScreen(navController = navController)}
                    composable("scanscreen"){ScanScreen(navController = navController)}
                    composable("controlscreen"){ControlScreen(navController = navController)}
                    composable("settingsscreen"){ SettingsScreen(navController = navController)}
                    composable("chartscreen"){
                        ChartScreenNew(navController = navController)
                    }
                    composable("simplescreen"){
                        PressureActionsScreen()
                    }
                }
            }
        }
    }
}
