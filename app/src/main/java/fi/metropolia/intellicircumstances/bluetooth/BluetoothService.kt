package fi.metropolia.intellicircumstances.bluetooth

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.ruuvi.station.bluetooth.IRuuviGattListener
import com.ruuvi.station.bluetooth.LogReading
import fi.metropolia.intellicircumstances.bluetooth.decoder.FoundTag
import kotlinx.coroutines.*
import java.util.*
@SuppressLint("LogNotTimber")
class BluetoothService: Service(), RuuviTagScanner.OnTagFoundListener, IRuuviGattListener {
    private val binder = LocalBinder()
    val foundTags = MutableLiveData<List<FoundTag>>()
    val newData = MutableLiveData<LogReading>()
    private lateinit var ruuviRangeNotifier: RuuviTagScanner

    override fun onCreate() {
        super.onCreate()
        ruuviRangeNotifier = RuuviTagScanner(applicationContext)
        GlobalScope.launch(Dispatchers.IO) {
            startScanning()
            delay(5000L)
            stopScanning()
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


    fun connect(mac: String, logDate: Date): Boolean {
        Log.d("DBG","trying to connect to device $mac and get logs from $logDate")
        return ruuviRangeNotifier.connect(mac, logDate, this)
    }

    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): BluetoothService = this@BluetoothService
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return binder
    }

    override fun connected(state: Boolean) {
        Log.d("DBG","bt device connected state $state")

    }

    override fun dataReady(data: List<LogReading>) {
        Log.d("DBG","bt data $data")
        if (data.isNotEmpty()) {
            newData.postValue(data.first())
        }
    }

    override fun deviceInfo(model: String, fw: String, canReadLogs: Boolean) {

    }

    override fun error(errorMessage: String) {

    }

    override fun heartbeat(raw: String) {

    }

    override fun syncProgress(syncedDataPoints: Int) {

    }
}