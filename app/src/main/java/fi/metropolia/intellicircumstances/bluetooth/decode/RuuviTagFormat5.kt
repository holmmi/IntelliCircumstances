package fi.metropolia.intellicircumstances.bluetooth.decode

class RuuviTagFormat5 {
    companion object {
        fun parseData(data: ByteArray): RuuviTagSensorData {
            val temperature = ((data[1].toUByte().toInt() shl 8) or data[2].toUByte().toInt()) * 0.005
            val humidity = ((data[3].toUByte().toUInt() shl 8) or data[4].toUByte().toUInt()).toDouble() / 40000 * 100
            val pressure = (((data[5].toUByte().toUInt() shl 8) or data[6].toUByte().toUInt()).toDouble() + 50000) / 100
            return RuuviTagSensorData(pressure, humidity, temperature)
        }
    }
}