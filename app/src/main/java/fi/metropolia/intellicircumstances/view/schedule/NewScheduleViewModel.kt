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

    private val _dateConstraints = MutableLiveData<CalendarConstraints?>()
    val dateConstraints: LiveData<CalendarConstraints?>
        get() = _dateConstraints

    fun setDateConstraints(date: Long?, isStartDate: Boolean) {
        var min: CalendarConstraints.DateValidator = DateValidatorPointForward.from(minDate)
        var max: CalendarConstraints.DateValidator = DateValidatorPointForward.from(maxDate)
        if (isStartDate) {
            min = DateValidatorPointForward.from(date ?: minDate)
            max = DateValidatorPointBackward.before(date?.plus(constraint) ?: maxDate)
        }
        if (!isStartDate) {
            min = DateValidatorPointForward.from(date?.minus(constraint) ?: minDate)
            max = DateValidatorPointBackward.before(date ?: maxDate)
        }

        val constraintsBuilderRange = CalendarConstraints.Builder()

        val listValidators = ArrayList<CalendarConstraints.DateValidator>()
        listValidators.add(min)
        listValidators.add(max)
        val validators = CompositeDateValidator.allOf(listValidators)
        constraintsBuilderRange.setValidator(validators)

        _dateConstraints.postValue(constraintsBuilderRange.build())
    }

    fun resetDateConstraints() {
        _dateConstraints.postValue(null)
    }

    companion object {
        private val minDate = 0L
        private val maxDate = 4102437600000L
        private val constraint = 777600000L
    }
}