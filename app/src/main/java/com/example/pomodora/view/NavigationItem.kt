package com.example.pomodora.view

import kotlinx.serialization.Serializable

sealed class NavigationItem {
    @Serializable
    object Dashboard : NavigationItem()

    @Serializable
    object LoginScreen: NavigationItem()

    @Serializable
    object SignUpScreen: NavigationItem()


}