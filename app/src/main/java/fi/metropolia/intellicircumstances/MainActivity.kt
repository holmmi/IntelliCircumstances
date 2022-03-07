package fi.metropolia.intellicircumstances

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.accompanist.pager.ExperimentalPagerApi
import fi.metropolia.intellicircumstances.view.main.MainView

@ExperimentalPagerApi
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
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
}