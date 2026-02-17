package com.example.pomodora.view

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pomodora.view.screens.LoginScreen
import com.example.pomodora.view.screens.MainDashboardScreen
import com.example.pomodora.view.screens.SignUpScreen
import com.example.pomodora.viewModel.AuthViewModel

@Composable
fun AppNavigation(viewModel: AuthViewModel) {
    val navController = rememberNavController()

    // Check current user session

    val startDestination = if (viewModel.isUserLoggedIn()) {
        NavigationItem.Dashboard
    } else {
        NavigationItem.LoginScreen
    }

    NavHost(navController = navController, startDestination = startDestination) {

        composable<NavigationItem.LoginScreen> {
            LoginScreen(navController = navController, viewModel = viewModel)
        }

        composable<NavigationItem.SignUpScreen> {
            SignUpScreen(navController = navController, viewModel =viewModel )
        }

        composable<NavigationItem.Dashboard> {
            MainDashboardScreen(navController = navController,viewModel)
        }
    }
}