package fi.metropolia.intellicircumstances.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SpaceDao {
    @Insert
    suspend fun addSpace(space: Space): Long

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

    @Query("SELECT * FROM space WHERE id = :spaceId")
    suspend fun getSpace(spaceId: Long): Space

    @Query("DELETE FROM space WHERE id = :spaceId")
    suspend fun deleteSpace(spaceId: Long)
}

@Dao
interface DeviceDao {
    @Insert
    suspend fun addDevice(ruuviDevice: RuuviDevice)

    @Query("DELETE FROM ruuvi_device WHERE space_id = :spaceId")
    suspend fun deleteDeviceBySpaceId(spaceId: Long)

    @Query("SELECT * FROM ruuvi_device WHERE space_id = :spaceId")
    suspend fun getRuuviTagDeviceBySpaceId(spaceId: Long): RuuviDevice?

    @Query("SELECT EXISTS(SELECT * FROM ruuvi_device WHERE space_id = :spaceId)")
    fun isDeviceAdded(spaceId: Long): LiveData<Boolean>
}

@Dao
interface ScheduleDao {
    @Insert
    suspend fun addSchedule(schedule: Schedule)

    @Query("SELECT * FROM schedule WHERE id = :scheduleId")
    fun getScheduleById(scheduleId: Long): Flow<Schedule>

    @Query("SELECT * FROM schedule WHERE space_id = :spaceId")
    fun getSchedulesBySpaceId(spaceId: Long): Flow<List<Schedule>>

    @Transaction
    @Query("SELECT * FROM schedule WHERE uuid = :uuid")
    suspend fun getScheduleAndDeviceByUuid(uuid: String): ScheduleAndRuuviDevice

    @Query("DELETE FROM schedule WHERE uuid = :uuid")
    suspend fun deleteScheduleByUuid(uuid: String)

    @Update
    suspend fun updateSchedule(schedule: Schedule)
}

@Dao
interface CircumstanceDao {
    @Insert
    suspend fun addCircumstances(circumstances: List<Circumstance>)

    @Query("SELECT * FROM circumstance WHERE schedule_id = :scheduleId")
    fun getCircumstancesByScheduleId(scheduleId: Long): Flow<List<Circumstance>>

    @Query("SELECT * FROM circumstance WHERE schedule_id = :scheduleId")
    suspend fun getCircumstancesByScheduleIdAsList(scheduleId: Long): List<Circumstance>
}

private const val DATABASE_NAME = "intelli"

@Database(
    entities = [
        Circumstance::class,
        Property::class,
        RuuviDevice::class,
        Schedule::class,
        Space::class
    ],
    version = 1,
    exportSchema = false
)
abstract class IntelliDatabase : RoomDatabase() {
    abstract fun circumstanceDao(): CircumstanceDao
    abstract fun deviceDao(): DeviceDao
    abstract fun scheduleDao(): ScheduleDao
    abstract fun spaceDao(): SpaceDao

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