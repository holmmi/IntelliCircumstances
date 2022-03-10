package fi.metropolia.intellicircumstances.view.schedule

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import fi.metropolia.intellicircumstances.database.Circumstance
import fi.metropolia.intellicircumstances.database.Schedule
import fi.metropolia.intellicircumstances.dto.FirebaseSchedule
import fi.metropolia.intellicircumstances.dto.FirebaseScheduleRecord
import fi.metropolia.intellicircumstances.repository.CircumstanceRepository
import fi.metropolia.intellicircumstances.repository.ScheduleRepository
import fi.metropolia.intellicircumstances.repository.ShareRepository
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.*

class ScheduleResultsViewModel(application: Application) : AndroidViewModel(application) {
    private val circumstanceRepository = CircumstanceRepository(application.applicationContext)
    private val scheduleRepository = ScheduleRepository(application.applicationContext)
    private val shareRepository = ShareRepository()

    private val _isScheduleShared = MutableLiveData<Boolean?>(null)
    val isScheduleShared: LiveData<Boolean?>
        get() = _isScheduleShared

    private val _shareSucceeded = MutableLiveData<Boolean?>(null)
    val shareSucceeded: LiveData<Boolean?>
        get() = _shareSucceeded

    fun getCircumstancesByScheduleId(scheduleId: Long): LiveData<List<Circumstance>> =
        circumstanceRepository.getCircumstancesByScheduleId(scheduleId)

    fun getScheduleById(scheduleId: Long): LiveData<Schedule> =
        scheduleRepository.getScheduleById(scheduleId)

    fun getFormattedDate(date: Long): String {
        val dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
        return dateFormat.format(Date(date))
    }

    fun checkIfScheduleIsShared(uuid: String) {
        viewModelScope.launch {
            _isScheduleShared.postValue(shareRepository.isScheduleShared(uuid))
        }
    }

    fun resetScheduleIsShared() {
        _isScheduleShared.value = null
    }

    fun resetSharedSucceed() {
        _shareSucceeded.value = null
    }

    fun shareSchedule(schedule: Schedule) {
        viewModelScope.launch {
            val circumstances = circumstanceRepository.getCircumstancesByScheduleIdAsList(schedule.id!!)
            val firebaseSchedule = FirebaseSchedule(
                uuid = schedule.uuid,
                name = schedule.name,
                startDate = schedule.startDate,
                endDate = schedule.endDate,
                records = circumstances.map {
                    FirebaseScheduleRecord(
                        date = it.time,
                        airPressure = it.airPressure,
                        humidity = it.humidity,
                        temperature = it.temperature
                    )
                }
            )
            _shareSucceeded.value = shareRepository.writeResults(firebaseSchedule)
        }
    }
}