package fi.metropolia.intellicircumstances.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import fi.metropolia.intellicircumstances.database.IntelliDatabase
import fi.metropolia.intellicircumstances.database.Schedule

class ScheduleRepository(context: Context) {
    private val scheduleDao = IntelliDatabase.getInstance(context).scheduleDao()

    suspend fun addSchedule(schedule: Schedule) {
        scheduleDao.addSchedule(schedule)
    }

    suspend fun deleteScheduleById(scheduleId: Long) {
        scheduleDao.deleteScheduleById(scheduleId)
    }

    fun getSchedulesBySpaceId(spaceId: Long): LiveData<List<Schedule>> =
        scheduleDao.getSchedulesBySpaceId(spaceId).asLiveData()

    suspend fun getScheduleByUuid(uuid: String) =
        scheduleDao.getScheduleByUuid(uuid)

    suspend fun updateSchedule(schedule: Schedule) =
        scheduleDao.updateSchedule(schedule)
}