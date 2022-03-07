package fi.metropolia.intellicircumstances.view.main

import android.app.Application
import android.content.res.Configuration
import androidx.compose.ui.text.intl.Locale
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import fi.metropolia.intellicircumstances.database.Setting
import fi.metropolia.intellicircumstances.repository.SettingRepository
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val settingRepository = SettingRepository(application.applicationContext)

    init {
        initializeSettings()
    }

    fun getSettings(): LiveData<Setting?> = settingRepository.getSettings()

    private fun initializeSettings() {
        val context = getApplication<Application>().applicationContext
        val uiMode = context.resources.configuration.uiMode
        val language = Locale.current.language

        viewModelScope.launch {
            if (settingRepository.getSettingsCount() == 0L) {
                settingRepository.addSettings(
                    Setting(
                        darkMode = uiMode == Configuration.UI_MODE_NIGHT_YES,
                        language = language
                    )
                )
            }
        }
    }
}