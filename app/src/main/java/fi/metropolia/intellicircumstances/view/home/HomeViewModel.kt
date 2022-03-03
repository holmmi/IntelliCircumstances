package fi.metropolia.intellicircumstances.view.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import fi.metropolia.intellicircumstances.dto.FirebaseSchedule
import fi.metropolia.intellicircumstances.repository.ShareRepository
import java.text.DateFormat
import java.util.*

class HomeViewModel : ViewModel() {
    private val shareRepository = ShareRepository(listenForChanges = true)

    fun getSharedSchedules(): LiveData<List<FirebaseSchedule>?> =
        shareRepository.sharedSchedules

    fun getFormattedDate(date: Long): String {
        val dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
        return dateFormat.format(Date(date))
    }
}