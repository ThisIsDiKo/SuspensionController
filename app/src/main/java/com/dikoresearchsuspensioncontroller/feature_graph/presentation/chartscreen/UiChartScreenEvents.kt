package com.dikoresearchsuspensioncontroller.feature_graph.presentation.chartscreen


sealed class UiChartScreenEvents{
    data class ShowSnackbar(val message: String): UiChartScreenEvents()
    data class NavigateTo(val destination: String): UiChartScreenEvents()
    object StartReadingSensors: UiChartScreenEvents()
}