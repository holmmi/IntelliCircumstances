package fi.metropolia.intellicircumstances.bluetooth.decode

data class RuuviTagSensorData(
    var time: Long? = null,
    var airPressure: Double? = null,
    var humidity: Double? = null,
    var temperature: Double? = null
)
