package fi.metropolia.intellicircumstances.view.spaces

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import fi.metropolia.intellicircumstances.bluetooth.*
import fi.metropolia.intellicircumstances.database.PropertyWithSpaces
import fi.metropolia.intellicircumstances.database.Space
import fi.metropolia.intellicircumstances.repository.DeviceRepository
import fi.metropolia.intellicircumstances.repository.SpaceRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SpacesViewModel(application: Application) : AndroidViewModel(application) {
    private val spaceRepository = SpaceRepository(application.applicationContext)
    private val deviceRepository = DeviceRepository(application.applicationContext)

    private val _ruuviTagDevices = MutableLiveData<List<RuuviTagDevice>?>(null)
    val ruuviTagDevices: LiveData<List<RuuviTagDevice>?>
        get() = _ruuviTagDevices

    private val scannerCallback = object : RuuviTagScannerCallback {
        override fun onDeviceFound(ruuviTagDevices: List<RuuviTagDevice>) {
            _ruuviTagDevices.postValue(ruuviTagDevices)
        }
    }

    private val ruuviTagScanner = RuuviTagScanner(application.applicationContext, scannerCallback)

    fun startScan(): Boolean {
        _ruuviTagDevices.value = null
        return ruuviTagScanner.startScan()
    }

    fun stopScan() = ruuviTagScanner.stopScan()

    fun getSpaces(propertyId: Long): LiveData<PropertyWithSpaces> =
        spaceRepository.getSpaces(propertyId)

    suspend fun addSpace(propertyId: Long, spaceName: String): Long =
        withContext(viewModelScope.coroutineContext) {
            spaceRepository.addSpace(
                Space(
                    propertyId = propertyId,
                    name = spaceName
                )
            )
        }

    fun deleteSpace(spaceId: Long) {
        viewModelScope.launch {
            spaceRepository.deleteSpace(spaceId)
        }
    }

    fun isDeviceAdded(spaceId: Long): LiveData<Boolean> = deviceRepository.isDeviceAdded(spaceId)

    fun addDevice(spaceId: Long, device: RuuviTagDevice) {
        viewModelScope.launch {
            deviceRepository.addDeviceToSpace(spaceId, device)
        }
    }
}