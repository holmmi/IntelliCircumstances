package fi.metropolia.intellicircumstances.view.measure

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import fi.metropolia.intellicircumstances.database.PropertyWithSpaces
import fi.metropolia.intellicircumstances.repository.SpaceRepository

class SpaceSelectionViewModel(application: Application) : AndroidViewModel(application) {
    private val spaceRepository = SpaceRepository(application.applicationContext)

    val propertiesWithSpaces: LiveData<List<PropertyWithSpaces>>
        get() = spaceRepository.getPropertiesWithSpaces()
}