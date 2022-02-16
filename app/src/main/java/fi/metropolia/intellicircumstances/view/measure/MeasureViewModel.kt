package fi.metropolia.intellicircumstances.view.measure

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import fi.metropolia.intellicircumstances.bluetooth.BluetoothService
import fi.metropolia.intellicircumstances.bluetooth.decoder.FoundTag
import fi.metropolia.intellicircumstances.database.PropertyWithSpaces
import fi.metropolia.intellicircumstances.repository.DeviceRepository
import fi.metropolia.intellicircumstances.repository.SpaceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*

class MeasureViewModel(
    application: Application,
    private val btService: BluetoothService,
    spaceId: Long?
) : AndroidViewModel(application) {
    val data = btService.newData
    val tagData = MutableLiveData<FoundTag>(null)
    val foundTags = btService.foundTags
    private val spaceRepository = SpaceRepository(application.applicationContext)
    private val deviceRepository = DeviceRepository(application.applicationContext)
    //val application = application

    init {
        initSpaceMeasure(spaceId)
    }

    fun initSpaceMeasure(spaceId: Long?) {
        var connected = false
        viewModelScope.launch(Dispatchers.IO) {
            val space = spaceId?.let { spaceRepository.getSpace(it) }

            Log.d("DBG", space.toString())
            if (space != null) {
                Log.d("DBG", "test")
                val device = space.deviceId?.let { deviceRepository.getSpaceAndDevice(it) }
                var mac: String = ""
                runBlocking(Dispatchers.IO) {
                    if (device != null) {
                        mac = device.map { it.device.macAddress }.first()
                    }
                }
            }
        }
        /*  foundTags.observe(this.life, Observer { tag ->

          })*/

    }

    val propertiesWithSpaces: LiveData<List<PropertyWithSpaces>>
        get() = spaceRepository.getPropertiesWithSpaces()
}