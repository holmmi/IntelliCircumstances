package fi.metropolia.intellicircumstances

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.accompanist.pager.ExperimentalPagerApi
import fi.metropolia.intellicircumstances.navigation.Navigation
import fi.metropolia.intellicircumstances.ui.theme.IntelliCircumstancesTheme

@ExperimentalPagerApi
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
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