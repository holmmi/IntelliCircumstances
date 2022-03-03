package fi.metropolia.intellicircumstances.view.settings

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import fi.metropolia.intellicircumstances.R
import fi.metropolia.intellicircumstances.extension.getActivity

class SettingsViewModel(application: Application, private val keys: Array<String>) :
    AndroidViewModel(application) {
    val app = application
    private var _isDarkMode = MutableLiveData<Boolean>(getDarkMode())
    val isDarkMode: LiveData<Boolean>
        get() = _isDarkMode

    init {

    }

    fun toggleDarkMode(key: String) {
        _isDarkMode.value = !_isDarkMode.value!!
        var mode: Int = 0
        if (isDarkMode.value == true) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            mode = AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            mode = AppCompatDelegate.MODE_NIGHT_NO
        }
        val sharedPref = app.getSharedPreferences("settings", Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putInt(key, mode)
            apply()
        }
    }

    private fun getDarkMode(): Boolean {
        val currentNightMode = Configuration.UI_MODE_NIGHT_MASK

        val sharedPref =
            app.getSharedPreferences("settings", Context.MODE_PRIVATE) ?: return false
        return (sharedPref.getInt(
            keys[0],
            currentNightMode
        )
                == AppCompatDelegate.MODE_NIGHT_YES)
    }

}