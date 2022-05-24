package com.dikoresearchsuspensioncontroller.feature_controller.presentation.scanscreen

sealed class UiEventScanScreen{
    data class NavigateTo(val destination: String): UiEventScanScreen()
}
