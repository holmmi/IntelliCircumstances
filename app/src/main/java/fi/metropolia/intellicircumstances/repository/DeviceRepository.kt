package fi.metropolia.intellicircumstances.repository

import android.content.Context
import androidx.lifecycle.LiveData
import fi.metropolia.intellicircumstances.bluetooth.RuuviTagDevice
import fi.metropolia.intellicircumstances.database.DeviceWithSpaces
import fi.metropolia.intellicircumstances.database.IntelliDatabase
import fi.metropolia.intellicircumstances.database.RuuviDevice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class DeviceRepository(context: Context) {
    private val deviceDao = IntelliDatabase.getInstance(context).deviceDao()
    private val scheduleDao = IntelliDatabase.getInstance(context).scheduleDao()

    suspend fun addDeviceToSpace(spaceId: Long, ruuviTagDevice: RuuviTagDevice) =
        withContext(Dispatchers.IO) {
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

    fun getRuuviTagDeviceAsLiveData(spaceId: Long): LiveData<RuuviDevice> =
        deviceDao.getRuuviTagDeviceAsLiveData(spaceId)

    fun isDeviceAdded(spaceId: Long) =
        deviceDao.isDeviceAdded(spaceId)

    suspend fun deleteDeviceBySpaceId(spaceId: Long) = withContext(Dispatchers.IO) {
        deviceDao.deleteDeviceBySpaceId(spaceId)
    }

    suspend fun deleteDevice(deviceId: Long) = withContext(Dispatchers.IO) {
        //get all spaces where the device is allocated
        val spaces =
            withContext(Dispatchers.IO) { deviceDao.getDeviceSpaces(deviceId).first().spaces }
        //delete all schedules for spaces with the device
        spaces.forEach { space ->
            space.id?.let { id -> scheduleDao.deleteScheduleBySpaceId(id) }
        }
        deviceDao.deleteDeviceById(deviceId)
    }

    suspend fun getDeviceSpaces(deviceId: Long): Flow<DeviceWithSpaces> =
        withContext(Dispatchers.IO) {
            deviceDao.getDeviceSpaces(deviceId)
        }
}