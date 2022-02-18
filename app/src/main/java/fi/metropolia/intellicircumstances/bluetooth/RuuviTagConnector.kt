package fi.metropolia.intellicircumstances.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Context
import android.os.Build
import android.util.Log
import fi.metropolia.intellicircumstances.bluetooth.decode.RuuviTagFormat
import fi.metropolia.intellicircumstances.bluetooth.decode.RuuviTagSensorData
import fi.metropolia.intellicircumstances.extension.toByteArray
import fi.metropolia.intellicircumstances.extension.toHex
import fi.metropolia.intellicircumstances.extension.toInt32
import java.util.*

@SuppressLint("MissingPermission")
class RuuviTagConnector(
    private val context: Context,
    private val ruuviTagConnectionCallback: RuuviTagConnectionCallback
) {
    private val bluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter = bluetoothManager.adapter
    private var bluetoothGatt: BluetoothGatt? = null
    private var writeCharacteristic: BluetoothGattCharacteristic? = null

    private var isReadingLogs = false
    private var sensorLogs = mutableListOf<RuuviTagSensorData>()

    private var ruuviFwVersion: Double? = null

    private val bluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                gatt?.discoverServices()
                ruuviTagConnectionCallback.onConnectionStateChange(ConnectionState.CONNECTED)
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                ruuviTagConnectionCallback.onConnectionStateChange(ConnectionState.DISCONNECTED)
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            gatt?.services?.forEach { service ->
                service.characteristics.forEach { characteristic ->
                    Log.d("DBG", "${characteristic.uuid}")
                    when (characteristic.uuid) {
                        READ_CHARACTERISTIC_UUID -> {
                            Log.d("DBG", "${characteristic.uuid}")
                            gatt.setCharacteristicNotification(characteristic, true)
                            val descriptor =
                                characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG)
                            descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                            gatt.writeDescriptor(descriptor)
                        }
                        WRITE_CHARACTERISTIC_UUID -> {
                            writeCharacteristic = characteristic
                        }
                        //TODO: Find why this causes live measurements not to be sent
                        /*  FIRMWARE_CHARACTERISTIC_UUID -> {
                             gatt.readCharacteristic(characteristic)
                         }*/
                    }
                }
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?
        ) {
            super.onCharacteristicChanged(gatt, characteristic)
            if (characteristic?.uuid == READ_CHARACTERISTIC_UUID) {
                handleRuuviNotification(characteristic)
            }
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            if (characteristic?.uuid == WRITE_CHARACTERISTIC_UUID && status == BluetoothGatt.GATT_SUCCESS) {
                isReadingLogs = true
                sensorLogs = mutableListOf()
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, status)
            if (characteristic?.uuid == FIRMWARE_CHARACTERISTIC_UUID && status == BluetoothGatt.GATT_SUCCESS) {
                val version = characteristic?.value?.decodeToString()
                version?.let {
                    ruuviFwVersion = it.substring(10, 14).toDoubleOrNull()
                }
            }
        }
    }

    fun connectDevice(macAddress: String) {
        if (isBluetoothEnabled() && bluetoothGatt == null) {
            val device = bluetoothAdapter.getRemoteDevice(macAddress)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                bluetoothGatt = device.connectGatt(
                    context,
                    false,
                    bluetoothGattCallback,
                    BluetoothDevice.TRANSPORT_LE
                )
            } else {
                bluetoothGatt = device.connectGatt(
                    context,
                    false,
                    bluetoothGattCallback
                )
            }
            val connected = bluetoothGatt?.connect()
            if (connected == false) {
                ruuviTagConnectionCallback.onConnectionStateChange(ConnectionState.CONNECTION_FAILED)
            }
        }
    }

    fun disconnectDevice() {
        bluetoothGatt?.close()
        bluetoothGatt = null
        ruuviTagConnectionCallback.onConnectionStateChange(ConnectionState.DISCONNECTED)
    }

    fun isBluetoothEnabled(): Boolean =
        bluetoothAdapter != null && bluetoothAdapter.isEnabled

    private fun handleRuuviNotification(characteristic: BluetoothGattCharacteristic?) {
        characteristic?.value?.let { data ->
            if (data.isNotEmpty()) {
                if (data[0] == FORMAT_5.toByte()) {
                    val parsedData = RuuviTagFormat.parseDataFromFormat5(data)
                    ruuviTagConnectionCallback.onReceiveSensorData(parsedData)
                } else {
                    if (isReadingLogs) {
                        if (data.toHex().endsWith("FFFFFFFFFFFFFFFF", true)) {
                            isReadingLogs = false
                            ruuviTagConnectionCallback.onReceiveSensorLogs(sensorLogs.toList())
                        } else {
                            val type = data.copyOfRange(0, 3)
                            val timestamp = data.copyOfRange(3, 7)
                            val value = data.copyOfRange(7, 11)
                            val time = timestamp.toInt32().toLong() * 1000

                            val logEntry =
                                sensorLogs.find { it.time == time } ?: RuuviTagSensorData()

                            when (type.toHex()) {
                                "3a3010" -> {
                                    val temperature = value.toInt32() / 100.0
                                    logEntry.temperature = temperature
                                }
                                "3a3110" -> {
                                    val humidity = value.toInt32() / 100.0
                                    logEntry.humidity = humidity
                                }
                                "3a3210" -> {
                                    val pressure = value.toInt32() / 100.0
                                    logEntry.airPressure = pressure
                                }
                            }
                            if (logEntry.time == null) {
                                logEntry.time = time
                                sensorLogs.add(logEntry)
                            }
                        }
                    }
                }
            }
        }
    }

    fun readLogs(from: Long? = null) {
        if (canReadLogs() && writeCharacteristic != null) {
            val to = System.currentTimeMillis() / 1000
            val headerBytes = byteArrayOf(0x3A, 0x3A, 0x11).copyOfRange(0, 3)
            val toBytes = to.toByteArray().copyOfRange(4, 8)
            val fromBytes = from?.toByteArray()?.copyOfRange(4, 8)
                ?: (System.currentTimeMillis() / 1000 - 60 * 60 * 24).toByteArray()
                    .copyOfRange(4, 8)
            val data = headerBytes + toBytes + fromBytes
            writeCharacteristic?.value = data
            writeCharacteristic?.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
            bluetoothGatt?.writeCharacteristic(writeCharacteristic)
        }
    }

    private fun canReadLogs(): Boolean {
        ruuviFwVersion?.let {
            return it >= LOGGING_CAPABLE_MIN_VERSION
        }
        return false
    }

    companion object {
        private val FIRMWARE_CHARACTERISTIC_UUID =
            UUID.fromString("00002A26-0000-1000-8000-00805F9B34FB")
        private val READ_CHARACTERISTIC_UUID =
            UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E")
        private val WRITE_CHARACTERISTIC_UUID =
            UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E")
        private val CLIENT_CHARACTERISTIC_CONFIG =
            UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
        private const val FORMAT_5 = 5
        private const val LOGGING_CAPABLE_MIN_VERSION = 3.30
    }
}