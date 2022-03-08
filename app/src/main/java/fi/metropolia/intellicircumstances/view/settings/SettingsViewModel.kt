package fi.metropolia.intellicircumstances.view.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import fi.metropolia.intellicircumstances.database.Setting
import fi.metropolia.intellicircumstances.repository.SettingRepository
import kotlinx.coroutines.launch
import java.util.*

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val settingRepository = SettingRepository(application.applicationContext)

    fun getSettings(): LiveData<Setting?> = settingRepository.getSettings()

    fun toggleDarkMode(setting: Setting) {
        viewModelScope.launch {
            settingRepository.updateSettings(
                Setting(setting.id, !setting.darkMode, setting.language))
        }
    }

    fun updateLanguage(setting: Setting, localeIndex: Int) {
        val context = getApplication<Application>().applicationContext
        val locale = context.resources.configuration.locales.get(localeIndex)
        viewModelScope.launch {
            settingRepository.updateSettings(
                Setting(setting.id, setting.darkMode, locale.toLanguageTag())
            )
        }
    }

    fun getCurrentLocale(languageTag: String): String {
        val locale = Locale.forLanguageTag(languageTag)
        return locale.displayName
    }

    fun getAvailableLocales(): List<String> {
        val context = getApplication<Application>().applicationContext
        val tags = context.resources.configuration.locales.toLanguageTags()
            .replace(",", " ")
            .split(" ")
        return tags.map {
            val locale = Locale.forLanguageTag(it)
            locale.displayName
        }
    }
}