package fi.metropolia.intellicircumstances.database

import androidx.room.*

@Entity(tableName = "property")
data class Property(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val name: String
)

@Entity(
    tableName = "space",
    foreignKeys = [
        ForeignKey(
            entity = Property::class,
            parentColumns = ["id"],
            childColumns = ["property_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = RuuviDevice::class,
            parentColumns = ["id"],
            childColumns = ["device_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["property_id"]),
        Index(value = ["device_id"])
    ]
)
data class Space(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo(name = "property_id") val propertyId: Long,
    val name: String,
    @ColumnInfo(name = "device_id") val deviceId: Long? = null
)

@Entity(
    tableName = "air_pressure",
    foreignKeys = [
        ForeignKey(
            entity = Space::class,
            parentColumns = ["id"],
            childColumns = ["space_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["space_id"])]
)
data class AirPressure(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo(name = "space_id") val spaceId: Long,
    val timestamp: Long? = System.currentTimeMillis(),
    val value: Float
)

@Entity(
    tableName = "humidity",
    foreignKeys = [
        ForeignKey(
            entity = Space::class,
            parentColumns = ["id"],
            childColumns = ["space_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["space_id"])]
)
data class Humidity(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo(name = "space_id") val spaceId: Long,
    val timestamp: Long? = System.currentTimeMillis(),
    val value: Float
)

@Entity(
    tableName = "temperature",
    foreignKeys = [
        ForeignKey(
            entity = Space::class,
            parentColumns = ["id"],
            childColumns = ["space_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["space_id"])]
)
data class Temperature(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo(name = "space_id") val spaceId: Long,
    val timestamp: Long? = System.currentTimeMillis(),
    val value: Float
)

@Entity(tableName = "ruuvi_device")
data class RuuviDevice(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo(name = "mac_address")
    val macAddress: String,
    val name: String?,
    val description: String?
)

data class PropertyWithSpaces(
    @Embedded val property: Property,
    @Relation(
        parentColumn = "id",
        entityColumn = "property_id"
    )
    val spaces: List<Space>
)

data class SpaceWithConditions(
    @Embedded val space: Space,
    @Relation(
        parentColumn = "id",
        entityColumn = "space_id"
    )
    val airPressures: List<AirPressure>,
    @Relation(
        parentColumn = "id",
        entityColumn = "space_id"
    )
    val humidities: List<Humidity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "space_id"
    )
    val temperatures: List<Temperature>
)

data class SpaceAndDevice(
    @Embedded val space: Space,
    @Relation(
        parentColumn = "device_id",
        entityColumn = "id"
    )
    val device: RuuviDevice
)