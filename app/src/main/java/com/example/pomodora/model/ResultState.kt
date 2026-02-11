package com.example.pomodora.model

sealed class ResultState<out T> {
    object Idle : ResultState<Nothing>()
    data class Success<T>(val data:T): ResultState<T>()
    data class Error(val message: String): ResultState<Nothing>()
    object Loading: ResultState<Nothing>()

}