package fi.metropolia.intellicircumstances.view.schedule

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.CompositeDateValidator
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.DateValidatorPointForward

class ScheduleViewModel() : ViewModel() {
    var dateConstraints = MutableLiveData<CalendarConstraints?>(null)

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

        dateConstraints.postValue(constraintsBuilderRange.build())
    }

    fun resetDateConstraints() {
        dateConstraints.postValue(null)
    }

    companion object {
        private val minDate = 0L
        private val maxDate = 4102437600000L
        private val constraint = 777600000L
    }
}