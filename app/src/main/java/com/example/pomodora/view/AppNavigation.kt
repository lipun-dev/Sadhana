package com.example.pomodora.view

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut

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

    NavHost(navController = navController, startDestination = startDestination,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(400, easing = FastOutSlowInEasing)
            ) + fadeIn(animationSpec = tween(400))
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(400, easing = FastOutSlowInEasing)
            ) + fadeOut(animationSpec = tween(400))
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(400, easing = FastOutSlowInEasing)
            ) + fadeIn(animationSpec = tween(400))
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(400, easing = FastOutSlowInEasing)
            ) + fadeOut(animationSpec = tween(400))
        }) {

        composable<NavigationItem.LoginScreen> {
            LoginScreen(navController = navController, viewModel = viewModel)
        }

        composable<NavigationItem.SignUpScreen> {
            SignUpScreen(navController = navController, viewModel =viewModel )
        }

        composable<NavigationItem.Dashboard>(
            enterTransition = {
                scaleIn(
                    initialScale = 0.9f,
                    animationSpec = tween(500, easing = FastOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(500))
            },
            exitTransition = {
                scaleOut(
                    targetScale = 0.9f,
                    animationSpec = tween(500, easing = FastOutSlowInEasing)
                ) + fadeOut(animationSpec = tween(500))
            },
            // Prevent sliding back if user logs out; just fade out
            popExitTransition = {
                fadeOut(animationSpec = tween(400))
            }
        ) {
            MainDashboardScreen(navController = navController,viewModel, statsViewModel = statsViewModel)
        }
    }
}