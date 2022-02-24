package fi.metropolia.intellicircumstances.bluetooth

import fi.metropolia.intellicircumstances.bluetooth.decode.RuuviTagSensorData

interface RuuviTagScannerCallback {
    fun onDeviceFound(ruuviTagDevices: List<RuuviTagDevice>)
}

interface RuuviTagConnectionCallback {
    fun onConnectionStateChange(connectionState: ConnectionState)

    fun onReceiveSensorData(ruuviTagSensorData: RuuviTagSensorData)

    fun onReceiveSensorLogs(logData: List<RuuviTagSensorData>)
}