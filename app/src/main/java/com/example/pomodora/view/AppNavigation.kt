package com.example.pomodora.view

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pomodora.PomoApp
import com.example.pomodora.ui.theme.AppBackground
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

    NavHost(navController = navController, startDestination = startDestination,
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground),
        enterTransition = {defaultEnterTransition()},
        exitTransition = {defaultExitTransition()},
        popEnterTransition = {defaultPopEnterTransition()},
        popExitTransition = {defaultPopExitTransition()}
    ) {

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
                    animationSpec = tween(600, easing = FastOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(600))
            },
            exitTransition = {
                scaleOut(
                    targetScale = 0.9f,
                    animationSpec = tween(600, easing = FastOutSlowInEasing)
                ) + fadeOut(animationSpec = tween(600))
            },
            // Prevent sliding back if user logs out; just fade out
            popExitTransition = {
                fadeOut(animationSpec = tween(600))
            }
        ) {
            MainDashboardScreen(navController = navController,viewModel, statsViewModel = statsViewModel)
        }
    }
}


fun defaultEnterTransition(
    duration: Int = 600
): EnterTransition {
    return slideInHorizontally(
        animationSpec = tween(duration, easing = FastOutSlowInEasing),
        initialOffsetX = { fullWidth -> fullWidth }
    ) + fadeIn(animationSpec = tween(100))
}

fun defaultExitTransition(
    duration: Int = 600
): ExitTransition {
    return slideOutHorizontally(
        animationSpec = tween(duration, easing = FastOutSlowInEasing),
        targetOffsetX = { fullWidth -> -fullWidth }
    ) + fadeOut(animationSpec = tween(duration))
}

fun defaultPopEnterTransition(
    duration: Int = 600
): EnterTransition {
    return slideInHorizontally(
        animationSpec = tween(duration, easing = FastOutSlowInEasing),
        initialOffsetX = { fullWidth -> -fullWidth }
    ) + fadeIn(animationSpec = tween(100))
}

fun defaultPopExitTransition(
    duration: Int = 600
): ExitTransition {
    return slideOutHorizontally(
        animationSpec = tween(duration),
        targetOffsetX = { fullWidth -> fullWidth }
    ) + fadeOut(animationSpec = tween(duration))
}