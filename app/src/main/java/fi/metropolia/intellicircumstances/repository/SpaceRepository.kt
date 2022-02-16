package fi.metropolia.intellicircumstances.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import fi.metropolia.intellicircumstances.database.IntelliDatabase
import fi.metropolia.intellicircumstances.database.Property
import fi.metropolia.intellicircumstances.database.PropertyWithSpaces
import fi.metropolia.intellicircumstances.database.Space
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SpaceRepository(context: Context) {
    private val spaceDao = IntelliDatabase.getInstance(context).spaceDao()

    suspend fun addProperty(property: Property) = withContext(Dispatchers.IO) {
        spaceDao.addProperty(property)
    }

    fun getProperties(): LiveData<List<Property>> = spaceDao.getProperties().asLiveData()

    fun getPropertiesWithSpaces(): LiveData<List<PropertyWithSpaces>> =
        spaceDao.getPropertiesWithSpaces().asLiveData()

    suspend fun deletePropertyById(propertyId: Long) = withContext(Dispatchers.IO) {
        spaceDao.deletePropertyById(propertyId)
    }

    suspend fun addSpace(space: Space) = withContext(Dispatchers.IO) {
        spaceDao.addSpace(space)
    }

    suspend fun getSpace(spaceId: Long): Space = withContext(Dispatchers.IO) {
        spaceDao.getSpace(spaceId)
    }

    fun getSpaces(propertyId: Long): LiveData<PropertyWithSpaces> =
        spaceDao.getPropertyWithSpaces(propertyId).asLiveData()


    suspend fun deleteSpace(spaceId: Long) = withContext(Dispatchers.IO) {
        spaceDao.deleteSpace(spaceId)
    }
}