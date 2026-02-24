package com.example.pomodora.repo

import com.example.pomodora.model.FocusEntry
import com.example.pomodora.model.STATS
import com.example.pomodora.model.USERS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.time.LocalDate

class StatsRepository(private val db: FirebaseFirestore,
    private val auth: FirebaseAuth) {
    fun getYearlyActivity(year: String): Flow<List<FocusEntry>> = callbackFlow {
        val uid = auth.currentUser?.uid?:run {
            close()
            return@callbackFlow
        }
        val docRef = db.collection(USERS).document(uid).collection(STATS).document(year)

        val listener = docRef.addSnapshotListener { snapshot, _ ->
            if (snapshot != null && snapshot.exists()) {
                val rawData = snapshot.data ?: emptyMap()
                val entries = rawData.mapNotNull { (key, value) ->
                    val dateStr = key.replace("dailyActivity.", "") // "02-07"
                    try {
                        // Create LocalDate (e.g., 2026-02-07)
                        val date = LocalDate.parse("$year-$dateStr")
                        FocusEntry(date, (value as Long).toInt())
                    } catch (_: Exception) { null }
                }
                trySend(entries.sortedBy { it.date })
            }
        }
        awaitClose { listener.remove() }
    }
}