package fi.metropolia.intellicircumstances.view.spaces

import android.annotation.SuppressLint
import android.app.Application
import android.content.*
import android.os.IBinder
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import fi.metropolia.intellicircumstances.bluetooth.BluetoothService
import fi.metropolia.intellicircumstances.bluetooth.RuuviTagScanner
import fi.metropolia.intellicircumstances.bluetooth.decoder.FoundTag
import fi.metropolia.intellicircumstances.database.PropertyWithSpaces
import fi.metropolia.intellicircumstances.database.RuuviDevice
import fi.metropolia.intellicircumstances.database.Space
import fi.metropolia.intellicircumstances.repository.DeviceRepository
import fi.metropolia.intellicircumstances.repository.SpaceRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("StaticFieldLeak")
class SpacesViewModel(application: Application, private val btService: BluetoothService) :
    AndroidViewModel(application) {
    private val spaceRepository = SpaceRepository(application.applicationContext)
    private val deviceRepository = DeviceRepository(application.applicationContext)

    val devices = btService.foundTags

    fun startScanning() {
        btService.startScanning()
    }

    fun stopScanning() {
        btService.stopScanning()
    }

    fun getSpaces(propertyId: Long): LiveData<PropertyWithSpaces> =
        spaceRepository.getSpaces(propertyId)

    fun addSpace(propertyId: Long, spaceName: String, deviceId: Long) {
        viewModelScope.launch {
            spaceRepository.addSpace(
                Space(
                    propertyId = propertyId,
                    name = spaceName,
                    deviceId = deviceId
                )
            )
        }
    }

    fun deleteSpace(spaceId: Long) {
        viewModelScope.launch {
            spaceRepository.deleteSpace(spaceId)
        }
    }

    suspend fun addDevice(device: RuuviDevice): Long =
        withContext(viewModelScope.coroutineContext) {
            deviceRepository.addDevice(device)
        }
}