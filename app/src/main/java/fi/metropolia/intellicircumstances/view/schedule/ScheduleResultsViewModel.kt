package fi.metropolia.intellicircumstances.view.schedule

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import fi.metropolia.intellicircumstances.database.Circumstance
import fi.metropolia.intellicircumstances.database.Schedule
import fi.metropolia.intellicircumstances.repository.CircumstanceRepository
import fi.metropolia.intellicircumstances.repository.ScheduleRepository
import java.text.DateFormat
import java.util.*

class ScheduleResultsViewModel(application: Application) : AndroidViewModel(application) {
    private val circumstanceRepository = CircumstanceRepository(application.applicationContext)
    private val scheduleRepository = ScheduleRepository(application.applicationContext)

    fun getCircumstancesByScheduleId(scheduleId: Long): LiveData<List<Circumstance>> =
        circumstanceRepository.getCircumstancesByScheduleId(scheduleId)

    fun getScheduleById(scheduleId: Long): LiveData<Schedule> =
        scheduleRepository.getScheduleById(scheduleId)

    fun getFormattedDate(date: Long): String {
        val dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
        return dateFormat.format(Date(date))
    }
}