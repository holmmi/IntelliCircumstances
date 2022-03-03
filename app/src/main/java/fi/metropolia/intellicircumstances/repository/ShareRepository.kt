package fi.metropolia.intellicircumstances.repository

import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import fi.metropolia.intellicircumstances.dto.FirebaseSchedule
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class ShareRepository(listenForChanges: Boolean = false) {
    private val database = FirebaseDatabase
        .getInstance(FIREBASE_DB_ADDRESS)
        .getReference()
        .child(SCHEDULE_PATH)

    private val sharedSchedules = MutableLiveData<List<FirebaseSchedule>?>(null)

    private val valueListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val schedules = snapshot.children.mapNotNull { it.getValue(FirebaseSchedule::class.java) }
            sharedSchedules.postValue(schedules)
        }

        override fun onCancelled(error: DatabaseError) {
            // Not used
        }
    }

    init {
        if (listenForChanges) {
            database.addValueEventListener(valueListener)
        }
    }

    suspend fun writeResults(firebaseSchedule: FirebaseSchedule): Boolean =
        suspendCancellableCoroutine { continuation ->
            val key = database.push().key
            if (key != null) {
                database.child(key).setValue(firebaseSchedule)
                    .addOnSuccessListener {
                        if (continuation.isActive) {
                            continuation.resume(true)
                        }
                    }
                    .addOnFailureListener {
                        if (continuation.isActive) {
                            continuation.resume(false)
                        }
                    }
            }
        }

    suspend fun isScheduleShared(uuid: String): Boolean =
        suspendCancellableCoroutine { continuation ->
            database.orderByChild("uuid").equalTo(uuid).get()
                .addOnSuccessListener {
                    if (continuation.isActive) {
                        continuation.resume(it.exists())
                    }
                }
                .addOnFailureListener {
                    if (continuation.isActive) {
                        continuation.resume(false)
                    }
                }
        }

    companion object {
        private const val FIREBASE_DB_ADDRESS = "https://intellicircumstances-default-rtdb.europe-west1.firebasedatabase.app/"
        private const val SCHEDULE_PATH = "schedule"
    }
}