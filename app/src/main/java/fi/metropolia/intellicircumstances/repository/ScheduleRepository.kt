package fi.metropolia.intellicircumstances.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import fi.metropolia.intellicircumstances.database.IntelliDatabase
import fi.metropolia.intellicircumstances.database.Schedule
import fi.metropolia.intellicircumstances.work.CollectRuuviTagLogsWorker
import java.util.*
import java.util.concurrent.TimeUnit

class ScheduleRepository(context: Context) {
    private val applicationContext = context
    private val scheduleDao = IntelliDatabase.getInstance(context).scheduleDao()

    suspend fun addSchedule(schedule: Schedule) {
        val workRequest = OneTimeWorkRequestBuilder<CollectRuuviTagLogsWorker>()
            .setInitialDelay(schedule.endDate - System.currentTimeMillis(), TimeUnit.MILLISECONDS)
            .build()
        WorkManager.getInstance(applicationContext).enqueue(workRequest)
        schedule.uuid = workRequest.id.toString()
        scheduleDao.addSchedule(schedule)
    }

    suspend fun deleteScheduleByUuid(uuid: String) {
        scheduleDao.deleteScheduleByUuid(uuid)
        val workManager = WorkManager.getInstance(applicationContext)
        workManager.cancelWorkById(UUID.fromString(uuid))
    }

    fun getScheduleById(scheduleId: Long) =
        scheduleDao.getScheduleById(scheduleId).asLiveData()

    fun getSchedulesBySpaceId(spaceId: Long): LiveData<List<Schedule>> =
        scheduleDao.getSchedulesBySpaceId(spaceId).asLiveData()

    suspend fun getScheduleAndDeviceByUuid(uuid: String) =
        scheduleDao.getScheduleAndDeviceByUuid(uuid)

    suspend fun updateSchedule(schedule: Schedule) =
        scheduleDao.updateSchedule(schedule)
}