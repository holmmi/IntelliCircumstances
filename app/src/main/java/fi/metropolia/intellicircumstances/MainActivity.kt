package fi.metropolia.intellicircumstances

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.accompanist.pager.ExperimentalPagerApi
import fi.metropolia.intellicircumstances.navigation.Navigation
import fi.metropolia.intellicircumstances.ui.theme.IntelliCircumstancesTheme

@ExperimentalPagerApi
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val currentNightMode = Configuration.UI_MODE_NIGHT_MASK

        val sharedPref = getSharedPreferences("settings", Context.MODE_PRIVATE) ?: return
        val nightMode =
            sharedPref.getInt(resources.getStringArray(R.array.units)[0], currentNightMode)
        AppCompatDelegate.setDefaultNightMode(nightMode)

        if (android.os.Build.VERSION.SDK_INT >= 31) {
            setTheme(R.style.Theme_IntelliCircumstances_NoActionBar)
        } else {
            installSplashScreen()
        }
        super.onCreate(savedInstanceState)
        setContent {
            IntelliCircumstancesTheme {
                Navigation()
            }
        }
    }
}