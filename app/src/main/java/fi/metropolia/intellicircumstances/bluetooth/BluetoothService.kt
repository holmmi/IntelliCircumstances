package fi.metropolia.intellicircumstances.bluetooth

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.MutableLiveData
import fi.metropolia.intellicircumstances.bluetooth.decoder.FoundTag
import fi.metropolia.intellicircumstances.repository.DeviceRepository
import fi.metropolia.intellicircumstances.repository.SpaceRepository

class BluetoothService: Service(), RuuviTagScanner.OnTagFoundListener {
    private val binder = LocalBinder()
    val foundTags = MutableLiveData<List<FoundTag>>()

    private val ruuviRangeNotifier = RuuviTagScanner(application.applicationContext)
    private val spaceRepository = SpaceRepository(application.applicationContext)
    private val deviceRepository = DeviceRepository(application.applicationContext)

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

    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): BluetoothService = this@BluetoothService
    }

    override fun onBind(p0: Intent?): IBinder? {
        return binder
    }


}