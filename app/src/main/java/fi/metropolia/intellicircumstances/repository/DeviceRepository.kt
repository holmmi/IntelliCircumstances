package fi.metropolia.intellicircumstances.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import fi.metropolia.intellicircumstances.bluetooth.RuuviTagDevice
import fi.metropolia.intellicircumstances.database.IntelliDatabase
import fi.metropolia.intellicircumstances.database.RuuviDevice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeviceRepository(context: Context) {
    private val deviceDao = IntelliDatabase.getInstance(context).deviceDao()

    suspend fun addDeviceToSpace(spaceId: Long, ruuviTagDevice: RuuviTagDevice) = withContext(Dispatchers.IO) {
        // Remove an existing device
        deviceDao.deleteDeviceBySpaceId(spaceId)
        // Add a new device
        deviceDao.addDevice(
            RuuviDevice(
                spaceId = spaceId,
                macAddress = ruuviTagDevice.macAddress,
                name = ruuviTagDevice.name
            )
        )
    }

    suspend fun getRuuviTagDeviceBySpaceId(spaceId: Long) = withContext(Dispatchers.IO) {
        deviceDao.getRuuviTagDeviceBySpaceId(spaceId)
    }

    fun isDeviceAdded(spaceId: Long) =
            deviceDao.isDeviceAdded(spaceId)
}