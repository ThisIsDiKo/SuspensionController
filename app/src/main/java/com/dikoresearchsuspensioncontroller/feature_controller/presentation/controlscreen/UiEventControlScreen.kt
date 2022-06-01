package com.dikoresearchsuspensioncontroller.feature_controller.presentation.controlscreen


sealed class UiEventControlScreen{
    data class ShowSnackbar(val message: String): UiEventControlScreen()
    data class NavigateTo(val destination: String): UiEventControlScreen()
    object StartReadingSensors: UiEventControlScreen()
}
