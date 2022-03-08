package fi.metropolia.intellicircumstances

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.google.accompanist.pager.ExperimentalPagerApi
import fi.metropolia.intellicircumstances.repository.SettingRepository
import fi.metropolia.intellicircumstances.view.main.MainView
import kotlinx.coroutines.launch
import java.util.*

@ExperimentalPagerApi
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setLanguage()
        setupSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            MainView()
        }
    }

    private fun setupSplashScreen() {
        if (android.os.Build.VERSION.SDK_INT >= 31) {
            setTheme(R.style.Theme_IntelliCircumstances_NoActionBar)
        } else {
            installSplashScreen()
        }
    }

    private fun setLanguage() {
        val resources = this.baseContext.resources
        val configuration = resources.configuration
        val settingRepository = SettingRepository(this.applicationContext)
        lifecycleScope.launch {
            val language = settingRepository.getLanguage()
            val locale = if (language != null) Locale.forLanguageTag(language) else Locale.getDefault()
            Locale.setDefault(locale)
            configuration.setLocale(locale)
            configuration.setLayoutDirection(locale)
            resources.updateConfiguration(configuration, resources.displayMetrics)
        }
    }
}