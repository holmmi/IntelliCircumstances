package fi.metropolia.intellicircumstances.view.spaces

import android.app.Application
import android.content.*
import android.os.IBinder
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

class SpacesViewModel(application: Application) : AndroidViewModel(application),
    RuuviTagScanner.OnTagFoundListener {
    val foundTags = MutableLiveData<List<FoundTag>>()
    private val ruuviRangeNotifier = RuuviTagScanner(application.applicationContext)
    private val spaceRepository = SpaceRepository(application.applicationContext)
    private val deviceRepository = DeviceRepository(application.applicationContext)
    private lateinit var mService: BluetoothService
    private var mBound: Boolean = false

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as BluetoothService.LocalBinder
            mService = binder.getService()
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }



        override fun onTagFound(tag: FoundTag) {
        val prevTags = foundTags.value
        if (prevTags != null && prevTags.size > 1) {
            foundTags.postValue(prevTags + tag)
        } else {
            foundTags.postValue(listOf(tag))
        }
    }

    fun startScanning() {

        ruuviRangeNotifier.startScanning(this)
    }

    fun stopScanning() {
        ruuviRangeNotifier.stopScanning()
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

    suspend fun addDevice(device: RuuviDevice): Long = withContext(viewModelScope.coroutineContext) {
        deviceRepository.addDevice(device)
    }
}