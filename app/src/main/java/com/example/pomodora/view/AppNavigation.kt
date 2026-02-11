package com.example.pomodora.view

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pomodora.view.screens.FocusScreen
import com.example.pomodora.view.screens.HomeScreen
import com.example.pomodora.view.screens.LoginScreen
import com.example.pomodora.view.screens.SignUpScreen
import com.example.pomodora.viewModel.AuthViewModel

@Composable
fun AppNavigation(viewModel: AuthViewModel) {
    val navController = rememberNavController()

    // Check current user session


    NavHost(navController = navController, startDestination = NavigationItem.LoginScreen) {

        composable<NavigationItem.LoginScreen> {
            LoginScreen(navController = navController, viewModel = viewModel)
        }

        composable<NavigationItem.SignUpScreen> {
            SignUpScreen(navController = navController, viewModel =viewModel )
        }

        composable<NavigationItem.HomeScreen> {
            HomeScreen(navController)
        }

        composable<NavigationItem.FocusScreen> {
            FocusScreen()
        }
    }
}