package fi.metropolia.intellicircumstances.bluetooth.decode

data class RuuviTagSensorData(
    val airPressure: Double,
    val humidity: Double,
    val temperature: Double
)
