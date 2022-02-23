package fi.metropolia.intellicircumstances.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import fi.metropolia.intellicircumstances.database.Circumstance
import fi.metropolia.intellicircumstances.database.IntelliDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CircumstanceRepository(context: Context) {
    private val circumstanceDao = IntelliDatabase.getInstance(context).circumstanceDao()

    suspend fun addSchedules(circumstances: List<Circumstance>) = withContext(Dispatchers.IO) {
        circumstanceDao.addCircumstances(circumstances)
    }

    fun getCircumstancesByScheduleId(scheduleId: Long): LiveData<List<Circumstance>> =
        circumstanceDao.getCircumstancesByScheduleId(scheduleId).asLiveData()
}