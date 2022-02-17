package fi.metropolia.intellicircumstances

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import fi.metropolia.intellicircumstances.bluetooth.BtWorker
import fi.metropolia.intellicircumstances.navigation.Navigation
import fi.metropolia.intellicircumstances.ui.theme.IntelliCircumstancesTheme
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val work = PeriodicWorkRequestBuilder<BtWorker>(1, TimeUnit.MINUTES)
            .setInputData(Data.Builder().putBoolean("isStart", true).build())
            .setInitialDelay(6000, TimeUnit.MILLISECONDS)
            .build()

        val workManager = WorkManager.getInstance(this)
        workManager.enqueue(work)

        setContent {
            IntelliCircumstancesTheme {
                Navigation()
            }
        }
    }
}