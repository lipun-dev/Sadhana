package com.example.pomodora.repo

import com.example.pomodora.model.ResultState
import com.example.pomodora.model.USERS
import com.example.pomodora.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepo{

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    val currentUser: FirebaseUser?
        get() = auth.currentUser
    // Login Function
    suspend fun loginUser(email: String, pass: String): ResultState<UserProfile> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, pass).await()
            val user = result.user

            if (user != null) {
                // Return Success with the UserProfile data
                ResultState.Success(UserProfile(userId = user.uid, email = user.email ?: ""))
            } else {
                ResultState.Error("User data not found")
            }
        } catch (e: Exception) {
            ResultState.Error(e.message ?: "An unknown error occurred")
        }
    }

    // Sign Up Function
    suspend fun signUpUser(email: String, pass: String): ResultState<UserProfile> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, pass).await()
            val user = result.user

            if (user != null) {
                val newUser = UserProfile(userId = user.uid, email = email)

                // Save to Firestore
                val userData = hashMapOf(
                    "email" to email,
                    "createdAt" to com.google.firebase.Timestamp.now(),
                    "totalFocusMinutes" to 0
                )
                firestore.collection(USERS).document(user.uid).set(userData).await()

                ResultState.Success(newUser)
            } else {
                ResultState.Error("Sign up failed")
            }
        } catch (e: Exception) {
            ResultState.Error(e.message ?: "An unknown error occurred")
        }
    }

    fun signOut() {
        auth.signOut()
    }
}