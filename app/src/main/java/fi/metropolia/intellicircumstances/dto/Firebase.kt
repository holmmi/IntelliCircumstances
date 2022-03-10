package fi.metropolia.intellicircumstances.dto

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class FirebaseSchedule(
    val uuid: String? = null,
    val name: String? = null,
    val startDate: Long? = null,
    val endDate: Long? = null,
    val records: List<FirebaseScheduleRecord>? = null
)

@IgnoreExtraProperties
data class FirebaseScheduleRecord(
    val date: Long? = null,
    val airPressure: Double? = null,
    val humidity: Double? = null,
    val temperature: Double? = null
)