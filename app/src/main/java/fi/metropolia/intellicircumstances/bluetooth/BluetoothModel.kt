package fi.metropolia.intellicircumstances.bluetooth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.ruuvi.station.bluetooth.FoundRuuviTag
import com.ruuvi.station.bluetooth.IRuuviTagScanner

class BluetoothModel(application: Application, ) :
    AndroidViewModel(application), IRuuviTagScanner.OnTagFoundListener {
    val foundTags = MutableLiveData<List<FoundRuuviTag>>()
    private val ruuviRangeNotifier = RuuviTagScanner(application.applicationContext)
    override fun onTagFound(tag: FoundRuuviTag) {
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

}