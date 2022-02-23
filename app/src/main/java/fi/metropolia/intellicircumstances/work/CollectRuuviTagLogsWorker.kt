package fi.metropolia.intellicircumstances.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkInfo
import androidx.work.WorkerParameters
import fi.metropolia.intellicircumstances.bluetooth.ConnectionState
import fi.metropolia.intellicircumstances.bluetooth.RuuviTagConnectionCallback
import fi.metropolia.intellicircumstances.bluetooth.RuuviTagConnector
import fi.metropolia.intellicircumstances.bluetooth.decode.RuuviTagSensorData
import fi.metropolia.intellicircumstances.repository.ScheduleRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class CollectRuuviTagLogsWorker(applicationContext: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(applicationContext, workerParameters) {
    private val scheduleRepository = ScheduleRepository(applicationContext)

    override suspend fun doWork(): Result {
        val scheduleAndRuuviDevice = scheduleRepository.getScheduleAndDeviceByUuid(id.toString())
        val schedule = scheduleAndRuuviDevice.schedule
        if (scheduleAndRuuviDevice.ruuviDevice != null) {
            val ruuviTagConnector = RuuviTagConnector(applicationContext)
            ruuviTagConnector.connectDevice(scheduleAndRuuviDevice.ruuviDevice.macAddress)
            delay(DELAY_BEFORE_READ)
            val logData = obtainLogs(ruuviTagConnector, scheduleAndRuuviDevice.schedule.startDate)
            ruuviTagConnector.disconnectDevice()
            if (logData != null && logData.isNotEmpty()) {
                schedule.status = WorkInfo.State.SUCCEEDED.name
                scheduleRepository.updateSchedule(schedule)
                return Result.success()
            }
        }
        schedule.status = WorkInfo.State.FAILED.name
        return Result.failure()
    }

    private suspend fun obtainLogs(
        ruuviTagConnector: RuuviTagConnector,
        startDate: Long
    ): List<RuuviTagSensorData>? =
        suspendCancellableCoroutine { continuation ->
            val connectionCallback = object : RuuviTagConnectionCallback {
                override fun onConnectionStateChange(connectionState: ConnectionState) {
                    if (connectionState == ConnectionState.DISCONNECTED || connectionState == ConnectionState.CONNECTION_FAILED) {
                        if (continuation.isActive) {
                            continuation.resume(null)
                        }
                    }
                }

                override fun onReceiveSensorData(ruuviTagSensorData: RuuviTagSensorData) {
                    // Not used
                }

                override fun onReceiveSensorLogs(logData: List<RuuviTagSensorData>) {
                    if (continuation.isActive) {
                        continuation.resume(logData)
                    }
                }
            }
            ruuviTagConnector.readLogs(startDate, connectionCallback)
        }

    companion object {
        private const val DELAY_BEFORE_READ = 10000L
    }
}