package fi.metropolia.intellicircumstances.component.resultview

import fi.metropolia.intellicircumstances.R

sealed class MeasurementTab(val tabName: Int) {
    object HumidityTab: MeasurementTab(R.string.humid)
    object PressureTab: MeasurementTab(R.string.pressure)
    object TemperatureTab: MeasurementTab(R.string.temp)
}