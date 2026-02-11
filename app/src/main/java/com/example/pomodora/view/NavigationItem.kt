package com.example.pomodora.view

import kotlinx.serialization.Serializable

sealed class NavigationItem {
    @Serializable
    object FocusScreen: NavigationItem()

    @Serializable
    object LoginScreen: NavigationItem()

    @Serializable
    object SignUpScreen: NavigationItem()

    @Serializable
    object HomeScreen: NavigationItem()
}