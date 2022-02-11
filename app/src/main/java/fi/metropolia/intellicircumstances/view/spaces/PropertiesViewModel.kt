package fi.metropolia.intellicircumstances.view.spaces

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import fi.metropolia.intellicircumstances.database.Property
import fi.metropolia.intellicircumstances.repository.SpaceRepository
import kotlinx.coroutines.launch

class PropertiesViewModel(application: Application) : AndroidViewModel(application) {
    private val spaceRepository = SpaceRepository(application.applicationContext)

    val properties: LiveData<List<Property>>
        get() = spaceRepository.getProperties()

    fun addProperty(propertyName: String) {
        viewModelScope.launch {
            spaceRepository.addProperty(Property(name = propertyName))
        }
    }

    fun deleteProperty(propertyId: Long) {
        viewModelScope.launch {
            spaceRepository.deletePropertyById(propertyId)
        }
    }
}