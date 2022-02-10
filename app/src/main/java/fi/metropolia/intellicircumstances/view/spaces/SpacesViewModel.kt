package fi.metropolia.intellicircumstances.view.spaces

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import fi.metropolia.intellicircumstances.database.IntelliDatabase

class SpacesViewModel(application: Application) : AndroidViewModel(application) {
    private val spaceDao = IntelliDatabase.getInstance(application.applicationContext).spaceDao()
}