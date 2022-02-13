package fi.metropolia.intellicircumstances.database

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SpaceDao {
    @Insert
    suspend fun addSpace(space: Space)

    @Insert
    suspend fun addProperty(property: Property)

    @Query("SELECT * FROM property")
    fun getProperties(): Flow<List<Property>>

    @Transaction
    @Query("SELECT * FROM property WHERE id = :propertyId")
    fun getPropertyWithSpaces(propertyId: Long): Flow<PropertyWithSpaces>

    @Transaction
    @Query("SELECT * FROM property")
    fun getPropertiesWithSpaces(): Flow<List<PropertyWithSpaces>>

    @Update
    suspend fun updateSpace(space: Space)

    @Query("DELETE FROM property WHERE id = :propertyId")
    suspend fun deletePropertyById(propertyId: Long)

    @Query("DELETE FROM space WHERE id = :spaceId")
    suspend fun deleteSpace(spaceId: Long)
}

@Dao
interface ConditionDao {
    @Insert
    suspend fun addAirPressure(airPressure: AirPressure)

    @Insert
    suspend fun addHumidity(humidity: Humidity)

    @Insert
    suspend fun addTemperature(temperature: Temperature)

    @Transaction
    @Query("SELECT * FROM space WHERE id = :spaceId")
    fun getSpaceWithConditions(spaceId: Long): Flow<SpaceWithConditions>
}

@Dao
interface DeviceDao {
    @Insert
    suspend fun addDevice(ruuviDevice: RuuviDevice)

    @Transaction
    @Query("SELECT * FROM space WHERE device_id = :deviceId")
    fun getSpaceAndDevice(deviceId: Long): Flow<SpaceAndDevice>
}

private const val DATABASE_NAME = "intelli"

@Database(
    entities = [
        Property::class,
        Space::class,
        AirPressure::class,
        Humidity::class,
        Temperature::class,
        RuuviDevice::class
    ],
    version = 1,
    exportSchema = false
)
abstract class IntelliDatabase : RoomDatabase() {
    abstract fun spaceDao(): SpaceDao
    abstract fun conditionDao(): ConditionDao
    abstract fun deviceDao(): DeviceDao

    companion object {
        @Volatile
        private var INSTANCE: IntelliDatabase? = null

        fun getInstance(context: Context): IntelliDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        IntelliDatabase::class.java,
                        DATABASE_NAME
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}