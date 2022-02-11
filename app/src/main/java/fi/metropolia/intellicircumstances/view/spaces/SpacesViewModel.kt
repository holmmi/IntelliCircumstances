package fi.metropolia.intellicircumstances.view.spaces

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import fi.metropolia.intellicircumstances.database.PropertyWithSpaces
import fi.metropolia.intellicircumstances.database.Space
import fi.metropolia.intellicircumstances.repository.SpaceRepository
import kotlinx.coroutines.launch

class SpacesViewModel(application: Application) : AndroidViewModel(application) {
    private val spaceRepository = SpaceRepository(application.applicationContext)

    fun getSpaces(propertyId: Long): LiveData<PropertyWithSpaces> =
        spaceRepository.getSpaces(propertyId)

    fun addSpace(propertyId: Long, spaceName: String) {
        viewModelScope.launch {
            spaceRepository.addSpace(Space(propertyId = propertyId, name = spaceName))
        }
    }

    fun deleteSpace(spaceId: Long) {
        viewModelScope.launch {
            spaceRepository.deleteSpace(spaceId)
        }
    }
}