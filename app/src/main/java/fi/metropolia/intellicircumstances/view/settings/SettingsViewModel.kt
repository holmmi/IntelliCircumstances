package fi.metropolia.intellicircumstances.view.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import fi.metropolia.intellicircumstances.database.Setting
import fi.metropolia.intellicircumstances.repository.SettingRepository
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val settingRepository = SettingRepository(application.applicationContext)

    fun getSettings(): LiveData<Setting?> = settingRepository.getSettings()

    fun toggleDarkMode(setting: Setting) {
        viewModelScope.launch {
            settingRepository.updateSettings(
                Setting(setting.id, !setting.darkMode, setting.language))
        }
    }
}