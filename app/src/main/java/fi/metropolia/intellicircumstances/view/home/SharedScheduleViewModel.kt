package fi.metropolia.intellicircumstances.view.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.metropolia.intellicircumstances.dto.FirebaseSchedule
import fi.metropolia.intellicircumstances.repository.ShareRepository
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.*

class SharedScheduleViewModel : ViewModel() {
    private val shareRepository = ShareRepository()

    private val _sharedSchedule = MutableLiveData<FirebaseSchedule?>(null)
    val sharedSchedule: LiveData<FirebaseSchedule?>
        get() = _sharedSchedule

    fun getScheduleByUuid(uuid: String) {
        viewModelScope.launch {
            _sharedSchedule.value = shareRepository.getScheduleByUuid(uuid)
        }
    }

    fun getFormattedDate(date: Long): String? {
        val dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
        return dateFormat.format(Date(date))
    }
}