package com.dikoresearchsuspensioncontroller.feature_controller.presentation.settingsscreen



sealed class UiEventSettingsScreen{
    data class NavigateTo(val destination: String): UiEventSettingsScreen()
    data class ShowSnackbar(val message: String): UiEventSettingsScreen()
    object NavigateUp : UiEventSettingsScreen()
}


