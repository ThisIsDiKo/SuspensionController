package com.dikoresearchsuspensioncontroller.feature_controller.presentation.startscreen

sealed class UiEventStartScreen{
    data class NavigateTo(val destination: String): UiEventStartScreen()
    data class MakeToast(val message: String): UiEventStartScreen()
}
