package fi.metropolia.intellicircumstances

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import fi.metropolia.intellicircumstances.ui.theme.IntelliCircumstancesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IntelliCircumstancesTheme {
            }
        }
    }
}