package com.dikoresearchsuspensioncontroller.feature_controller.presentation.scanscreen

sealed class UiEventScanScreen{
    data class ShowSnackbar(val message: String): UiEventScanScreen()
    data class NavigateTo(val destination: String): UiEventScanScreen()
}
