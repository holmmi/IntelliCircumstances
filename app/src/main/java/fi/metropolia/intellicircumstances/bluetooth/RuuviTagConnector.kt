package fi.metropolia.intellicircumstances.bluetooth

import android.bluetooth.*
import android.content.Context
import android.os.Build
import fi.metropolia.intellicircumstances.bluetooth.decode.RuuviTagFormat5
import java.util.*

class RuuviTagConnector(private val context: Context,
                        private val ruuviTagConnectionCallback: RuuviTagConnectionCallback) {
    private val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter = bluetoothManager.adapter
    private var bluetoothGatt: BluetoothGatt? = null

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
                    if (characteristic.uuid == CHARACTERISTIC_UUID) {
                        gatt.setCharacteristicNotification(characteristic, true)
                        val descriptor = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG)
                        descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                        gatt.writeDescriptor(descriptor)
                    }
                }
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?
        ) {
            super.onCharacteristicChanged(gatt, characteristic)
            if (characteristic?.uuid == CHARACTERISTIC_UUID) {
                handleRuuviNotification(characteristic)
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
                    val parsedData = RuuviTagFormat5.parseData(data)
                    ruuviTagConnectionCallback.onReceiveSensorData(parsedData)
                }
            }
        }
    }

    companion object {
        private val CHARACTERISTIC_UUID = UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E")
        private val CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
        private const val FORMAT_5 = 5
    }
}