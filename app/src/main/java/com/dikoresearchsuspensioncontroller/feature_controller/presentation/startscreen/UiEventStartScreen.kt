package com.dikoresearchsuspensioncontroller.feature_controller.presentation.startscreen

sealed class UiEventStartScreen{
    data class NavigateTo(val destination: String): UiEventStartScreen()
    data class ShowSnackbar(val message: String): UiEventStartScreen()
}
