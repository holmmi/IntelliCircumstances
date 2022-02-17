package fi.metropolia.intellicircumstances.bluetooth

import android.annotation.SuppressLint
import android.app.Service
import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import fi.metropolia.intellicircumstances.MainActivity
import fi.metropolia.intellicircumstances.bluetooth.decode.RuuviTagSensorData
import fi.metropolia.intellicircumstances.repository.DeviceRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/*class BluetoothHandlerService(private val context: Context) : Service() {
    val dueDate = Calendar.getInstance()

    // Set Execution around 05:00:00 AM
    fun setTime() {
        val currentDate = Calendar.getInstance()
        dueDate.set(Calendar.HOUR_OF_DAY, 5)
        dueDate.set(Calendar.MINUTE, 0)
        dueDate.set(Calendar.SECOND, 0)
        if (dueDate.before(currentDate)) {
            dueDate.add(Calendar.HOUR_OF_DAY, 24)
        }
        val timeDiff = dueDate.timeInMillis — currentDate.timeInMillis
        val dailyWorkRequest = OneTimeWorkRequestBuilder<BtWorkManager>
            .setConstraints(constraints).setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
            .addTag("TAG").build()
        WorkManager.getInstance(context).enqueue(dailyWorkRequest)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

}*/
@SuppressLint("LogNotTimber")
class BtWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {
    private val btUtil = BTUtil(appContext)

    companion object {
        private val hourFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        private var endHour = hourFormat.format(Date(99999999L))
        private var endDate = dateFormat.format(Date(99999999L))

        fun setEndTime(date: Date, hour: Date) {
            endHour = hourFormat.format(hour)
            endDate = dateFormat.format(date)
            Log.d("DBG", "$endHour Hrs $endDate")
        }
    }

    override fun doWork(): Result {
        return try {
            Log.d("DBG", "doWork")
            val curDate = dateFormat.format(Date())
            val curHour = hourFormat.format(Date())
            if (curDate == endDate && curHour == endHour) {
                btUtil.saveValues()
            }
            Result.retry()
        } catch (e: Error) {
            Log.i("DBG", "error " + e.message);
            Result.retry()
        }
    }
}

class BTUtil(context: Context) {
    @SuppressLint("LogNotTimber")
    private val scannerCallback = object : RuuviTagScannerCallback {
        override fun onScanComplete(ruuviTagDevices: List<RuuviTagDevice>) {
            _ruuviTagDevices.postValue(ruuviTagDevices)
        }
    }

    val deviceRepository = DeviceRepository(context.applicationContext)

    fun saveValues() {
        //scanDevices()
        Log.d("DBG", "saveValues")
        TODO("GET SAVED DATA FROM RUUVITAG AND SAVE TO DATABASE")
    }

    //
    private val _ruuviTagDevices = MutableLiveData<List<RuuviTagDevice>?>(null)
    val ruuviTagDevices: LiveData<List<RuuviTagDevice>?>
        get() = _ruuviTagDevices

    private val _ruuviConnectionState = MutableLiveData(ConnectionState.DISCONNECTED)
    val ruuviConnectionState: LiveData<ConnectionState>
        get() = _ruuviConnectionState


    private val connectionCallback = object : RuuviTagConnectionCallback {
        override fun onConnectionStateChange(connectionState: ConnectionState) {
            _ruuviConnectionState.postValue(connectionState)
        }

        override fun onReceiveSensorData(ruuviTagSensorData: RuuviTagSensorData) {

        }
    }
    val ruuviTagScanner =
        RuuviTagScanner(context.applicationContext, scannerCallback)
    val ruuviTagConnector =
        RuuviTagConnector(context.applicationContext, connectionCallback)


    fun scanDevices() {
        GlobalScope.launch(Dispatchers.IO) {
            if (ruuviTagScanner.startScan()) {
                delay(SCAN_TIMEOUT)
                ruuviTagScanner.stopScan()
            }
        }
    }

    fun addDeviceAndConnect(spaceId: Long, ruuviTagDevice: RuuviTagDevice) {
        GlobalScope.launch {
            deviceRepository.addDeviceToSpace(spaceId, ruuviTagDevice)
            ruuviTagConnector.connectDevice(ruuviTagDevice.macAddress)
        }
    }

    fun connectDevice(spaceId: Long) {
        GlobalScope.launch {
            val device = deviceRepository.getRuuviTagDeviceBySpaceId(spaceId)
            device?.macAddress?.let { ruuviTagConnector.connectDevice(it) }
        }
    }

    fun isBluetoothEnabled(): Flow<Boolean> = flow {
        while (true) {
            emit(ruuviTagConnector.isBluetoothEnabled())
            delay(CHECK_BLUETOOTH)
        }
    }

    fun disconnectDevice() {
        ruuviTagConnector.disconnectDevice()
    }

    val CHECK_BLUETOOTH = 1000L
    val SCAN_TIMEOUT = 5000L


}




