package fi.metropolia.intellicircumstances.database

import androidx.room.*
import androidx.work.WorkInfo

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
        )
    ],
    indices = [Index(value = ["property_id"])]
)
data class Space(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo(name = "property_id") val propertyId: Long,
    val name: String
)

data class PropertyWithSpaces(
    @Embedded val property: Property,
    @Relation(
        parentColumn = "id",
        entityColumn = "property_id"
    )
    val spaces: List<Space>
)

@Entity(
    tableName = "ruuvi_device",
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
data class RuuviDevice(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo(name = "space_id")
    val spaceId: Long,
    @ColumnInfo(name = "mac_address")
    val macAddress: String,
    val name: String? = null,
    val description: String? = null
)

@Entity(
    tableName = "schedule",
    foreignKeys = [
        ForeignKey(
            entity = Space::class,
            parentColumns = ["id"],
            childColumns = ["space_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["space_id"]),
        Index(value = ["uuid"])
    ]
)
data class Schedule(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo(name = "space_id") val spaceId: Long?,
    var uuid: String? = null,
    val name: String,
    @ColumnInfo(name = "start_date") val startDate: Long,
    @ColumnInfo(name = "end_date") val endDate: Long,
    var status: String = WorkInfo.State.ENQUEUED.name
)

data class ScheduleAndRuuviDevice(
    @Embedded val schedule: Schedule,
    @Relation(
        parentColumn = "space_id",
        entityColumn = "space_id"
    )
    val ruuviDevice: RuuviDevice?
)

@Entity(
    tableName = "circumstance",
    foreignKeys = [
        ForeignKey(
            entity = Schedule::class,
            parentColumns = ["id"],
            childColumns = ["schedule_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["schedule_id"])]
)
data class Circumstance(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo(name = "schedule_id") val scheduleId: Long,
    val time: Long?,
    val airPressure: Double?,
    val humidity: Double?,
    val temperature: Double?
)