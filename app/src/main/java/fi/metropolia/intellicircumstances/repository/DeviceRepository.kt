package fi.metropolia.intellicircumstances.repository

import android.content.Context
import fi.metropolia.intellicircumstances.database.IntelliDatabase
import fi.metropolia.intellicircumstances.database.RuuviDevice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeviceRepository(context: Context) {
    private val deviceDao = IntelliDatabase.getInstance(context).deviceDao()

    suspend fun addDevice(device: RuuviDevice): Long = withContext(Dispatchers.IO) {
        deviceDao.addDevice(device)
    }

   /* fun getSpaceAndDevice() {
        deviceDao.getSpaceAndDevice()
    }*/
}