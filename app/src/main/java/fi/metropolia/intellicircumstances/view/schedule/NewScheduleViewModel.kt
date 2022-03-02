package fi.metropolia.intellicircumstances.view.schedule

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.CompositeDateValidator
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.DateValidatorPointForward
import fi.metropolia.intellicircumstances.R
import fi.metropolia.intellicircumstances.database.Schedule
import fi.metropolia.intellicircumstances.repository.ScheduleRepository
import kotlinx.coroutines.launch
import java.util.*

class NewScheduleViewModel(application: Application) : AndroidViewModel(application) {
    private val scheduleRepository = ScheduleRepository(application.applicationContext)

    private val _formErrors = MutableLiveData<List<String>>()
    val formErrors: LiveData<List<String>>
        get() = _formErrors

    fun validateForm(
        spaceId: Long,
        scheduleName: String,
        startDate: Long,
        startHour: Int,
        startMinute: Int,
        endDate: Long,
        endHour: Int,
        endMinute: Int
    ): Boolean {
        _formErrors.value = mutableListOf()

        val resources = getApplication<Application>().applicationContext.resources
        val errors = mutableListOf<String>()
        if (scheduleName.isEmpty()) {
            errors.add(resources.getString(R.string.name_is_empty))
        }
        val startCalendar = Calendar.getInstance()
        startCalendar.time = Date(startDate)
        startCalendar.set(Calendar.HOUR_OF_DAY, startHour)
        startCalendar.set(Calendar.MINUTE, startMinute)

        val endCalendar = Calendar.getInstance()
        endCalendar.time = Date(endDate)
        endCalendar.set(Calendar.HOUR_OF_DAY, endHour)
        endCalendar.set(Calendar.MINUTE, endMinute)

        val timeDifference = endCalendar.time.time - startCalendar.time.time
        if (timeDifference < 0) {
            errors.add(resources.getString(R.string.end_date_before))
        }

        val daysBetween = timeDifference / 1000 / 60 / 60 / 24 * 1.0
        if (daysBetween > 10.0) {
            errors.add(resources.getString(R.string.date_range_error))
        }

        if (errors.isEmpty()) {
            addSchedule(spaceId, scheduleName, startCalendar.timeInMillis, endCalendar.timeInMillis)
        } else {
            _formErrors.value = errors.toList()
        }
        return errors.isEmpty()
    }

    private fun addSchedule(spaceId: Long, scheduleName: String, start: Long, end: Long) {
        viewModelScope.launch {
            scheduleRepository.addSchedule(
                Schedule(
                    spaceId = spaceId,
                    name = scheduleName,
                    startDate = start,
                    endDate = end
                )
            )
        }
    }

    fun getDateConstraints(startDate: Long?, isStartDate: Boolean): CalendarConstraints {
        val min: CalendarConstraints.DateValidator
        val max: CalendarConstraints.DateValidator

        if (isStartDate) {
            min = DateValidatorPointForward.from(MIN_DATE)
            max = DateValidatorPointBackward.before(MAX_DATE)
        } else {
            min = DateValidatorPointForward.from(startDate ?: MIN_DATE)
            max = DateValidatorPointBackward.before(startDate?.plus(MAX_DAYS) ?: MAX_DATE)
        }

        return CalendarConstraints.Builder()
            .setValidator(CompositeDateValidator.allOf(listOf(min, max)))
            .build()
    }

    companion object {
        private const val MIN_DATE = 0L
        // Current time + 10 years in ms
        private val MAX_DATE = System.currentTimeMillis() + 60 * 60 * 24 * 365 * 10 * 1000L
        // 10 days in ms
        private const val MAX_DAYS = 60 * 60 * 24 * 9 * 1000L
    }
}