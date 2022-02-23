package fi.metropolia.intellicircumstances.view.schedule

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import fi.metropolia.intellicircumstances.database.Schedule
import fi.metropolia.intellicircumstances.repository.ScheduleRepository
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.*

class SchedulesViewModel(application: Application) : AndroidViewModel(application) {
    private val scheduleRepository = ScheduleRepository(application.applicationContext)

    fun getSchedules(spaceId: Long): LiveData<List<Schedule>> =
        scheduleRepository.getSchedulesBySpaceId(spaceId)

    fun deleteSchedule(uuid: String) {
        viewModelScope.launch {
            scheduleRepository.deleteScheduleByUuid(uuid)
        }
    }

    fun formatDate(date: Long): String {
        val dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
        return dateFormat.format(Date(date))
    }
}