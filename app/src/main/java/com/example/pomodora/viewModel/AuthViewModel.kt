package com.example.pomodora.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pomodora.model.ResultState
import com.example.pomodora.model.UserProfile
import com.example.pomodora.repo.AuthRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val repository = AuthRepo()

    // Single source of truth for UI State
    private val _authState = MutableStateFlow<ResultState<UserProfile>>(ResultState.Idle)
    val authState: StateFlow<ResultState<UserProfile>> = _authState.asStateFlow()

    fun isUserLoggedIn(): Boolean {
        return repository.currentUser != null
    }

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _authState.value = ResultState.Error("Please fill all fields")
            return
        }

        _authState.value = ResultState.Loading
        viewModelScope.launch {
            _authState.value = repository.loginUser(email, pass)
        }
    }

    fun signUp(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _authState.value = ResultState.Error("Please fill all fields")
            return
        }

        _authState.value = ResultState.Loading
        viewModelScope.launch {
            _authState.value = repository.signUpUser(email, pass)
        }
    }

    fun logout() {
        repository.signOut()
        _authState.value = ResultState.Idle // Reset state
    }

    fun resetState() {
        _authState.value = ResultState.Idle
    }
}