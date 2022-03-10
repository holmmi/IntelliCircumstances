package fi.metropolia.intellicircumstances.view.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import fi.metropolia.intellicircumstances.database.PropertyWithSpaces
import fi.metropolia.intellicircumstances.repository.DeviceRepository
import fi.metropolia.intellicircumstances.repository.SpaceRepository
import kotlinx.coroutines.launch

class DevicesViewModel(application: Application) : AndroidViewModel(application) {
    private val spaceRepository = SpaceRepository(application.applicationContext)
    private val deviceRepository = DeviceRepository(application.applicationContext)

    val propertiesWithSpaces: LiveData<List<PropertyWithSpaces>>
        get() = spaceRepository.getPropertiesWithSpaces()

    fun getTag(spaceId: Long) =
        deviceRepository.getRuuviTagDeviceAsLiveData(spaceId)

    fun deleteDevice(deviceId: Long) {
        viewModelScope.launch {
            deviceRepository.deleteDevice(deviceId)
        }
    }
}