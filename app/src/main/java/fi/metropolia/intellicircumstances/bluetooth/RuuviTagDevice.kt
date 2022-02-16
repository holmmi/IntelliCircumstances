package fi.metropolia.intellicircumstances.bluetooth

data class RuuviTagDevice(
    val name: String,
    val macAddress: String,
    val rssi: Int
)
