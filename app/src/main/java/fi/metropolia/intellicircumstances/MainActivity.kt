package fi.metropolia.intellicircumstances

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import fi.metropolia.intellicircumstances.navigation.Navigation
import fi.metropolia.intellicircumstances.ui.theme.IntelliCircumstancesTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val asd = installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            IntelliCircumstancesTheme {
                Navigation()
            }
        }
    }
}