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
import com.dikoresearchsuspensioncontroller.feature_controller.presentation.startscreen.StartScreen
import com.dikoresearchsuspensioncontroller.ui.theme.SuspensionControllerTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }

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
                    composable("scanscreen"){}
                    composable("controlscreen"){}
                    composable("settingsescreen"){}
                }
            }
        }
    }
}
