package com.example.pomodora.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pomodora.PomoApp
import com.example.pomodora.view.screens.LoginScreen
import com.example.pomodora.view.screens.MainDashboardScreen
import com.example.pomodora.view.screens.SignUpScreen
import com.example.pomodora.viewModel.AuthViewModel
import com.example.pomodora.viewModel.StatsViewModel
import com.example.pomodora.viewModel.ViewModelFactory

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current.applicationContext as PomoApp
    val factory = ViewModelFactory(context)

    // Get instances using the factory
    val viewModel: AuthViewModel = viewModel(factory = factory)
    val statsViewModel : StatsViewModel = viewModel(factory = factory)
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
            MainDashboardScreen(navController = navController,viewModel, statsViewModel = statsViewModel)
        }
    }
}