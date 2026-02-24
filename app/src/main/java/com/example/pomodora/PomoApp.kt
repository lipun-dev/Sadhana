package com.example.pomodora

import android.app.Application
import com.example.pomodora.repo.AuthRepo
import com.example.pomodora.repo.SessionDbRepo
import com.example.pomodora.repo.StatsRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PomoApp : Application() {
    // Lazy initialization: Created only when needed
    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }

    val authRepo by lazy {
        AuthRepo(auth = auth, firestore = firestore)
    }

    val statsRepo by lazy {
        StatsRepository(db = firestore,
            auth = auth)
    }


    val repository by lazy {
        SessionDbRepo(
            firestore = firestore,
            auth = auth
        )
    }
}