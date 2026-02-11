package com.example.pomodora

import android.app.Application
import com.example.pomodora.repo.SessionDbRepo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PomoApp : Application() {
    // Lazy initialization: Created only when needed


    val repository by lazy {
        SessionDbRepo(
            firestore = FirebaseFirestore.getInstance(),
            auth = FirebaseAuth.getInstance()
        )
    }
}