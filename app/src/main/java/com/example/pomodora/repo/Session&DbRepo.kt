package com.example.pomodora.repo

import com.example.pomodora.model.SESSIONS
import com.example.pomodora.model.STATS
import com.example.pomodora.model.Session
import com.example.pomodora.model.USERS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SessionDbRepo(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {


    suspend fun saveSession(duration: Long, isSuccess: Boolean) {
        val uid = auth.currentUser?.uid ?: return
        val status = if (isSuccess) "COMPLETED" else "WITHERED"

        // 1. Save detailed log
        val session = Session(
            userId = uid,
            durationMinutes = duration,
            status = status
        )
        firestore.collection(USERS).document(uid)
            .collection(SESSIONS).add(session).await()

        // 2. Update Heatmap Aggregate (Only if success)
        if (isSuccess) {
            updateHeatmap(uid, duration)
        }
    }

    private suspend fun updateHeatmap(uid: String, duration: Long) {
        val currentYear = LocalDate.now().year.toString()
        val todayKey = LocalDate.now().format(DateTimeFormatter.ofPattern("MM-dd"))

        val statsRef = firestore.collection(USERS).document(uid)
            .collection(STATS).document(currentYear)

        // Atomic increment using Firestore FieldValue
        // We increment the specific day's minutes
        val updateData = hashMapOf<String, Any>(
            "dailyActivity.$todayKey" to com.google.firebase.firestore.FieldValue.increment(duration)
        )

        statsRef.set(updateData, SetOptions.merge()).await()
    }
}