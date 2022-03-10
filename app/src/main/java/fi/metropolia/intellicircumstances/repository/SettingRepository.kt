package fi.metropolia.intellicircumstances.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import fi.metropolia.intellicircumstances.database.IntelliDatabase
import fi.metropolia.intellicircumstances.database.Setting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SettingRepository(context: Context) {
    private val settingDao = IntelliDatabase.getInstance(context).settingDao()

    suspend fun addSettings(setting: Setting) = withContext(Dispatchers.IO) {
        settingDao.addSettings(setting)
    }

    fun getSettings(): LiveData<Setting?> = settingDao.getSettings().asLiveData()

    suspend fun getSettingsCount() = withContext(Dispatchers.IO) {
        settingDao.getSettingsCount()
    }

    suspend fun getLanguage() = withContext(Dispatchers.IO) {
        settingDao.getLanguage()
    }

    suspend fun updateSettings(setting: Setting) = withContext(Dispatchers.IO) {
        settingDao.updateSettings(setting)
    }
}