package com.example.pomodora.view.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pomodora.model.ResultState
import com.example.pomodora.ui.theme.MintAccent
import com.example.pomodora.view.NavigationItem
import com.example.pomodora.view.utils.WavyTextField
import com.example.pomodora.view.wavyAnimation.AuthScreenState
import com.example.pomodora.view.wavyAnimation.ForestButton
import com.example.pomodora.view.wavyAnimation.WavyAuthScaffold
import com.example.pomodora.viewModel.AuthViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    val authState by viewModel.authState.collectAsState()

    // Derive UI state from ViewModel state
    val screenState = remember(authState) {
        when (authState) {
            is ResultState.Loading -> AuthScreenState.Loading
            is ResultState.Success -> AuthScreenState.Success
            is ResultState.Error -> AuthScreenState.Failed
            else -> AuthScreenState.Input
        }
    }

    // Helper to navigate to dashboard
    val navigateToDashboard = {
        navController.navigate(NavigationItem.Dashboard) {
            popUpTo(0) { inclusive = true }
        }
    }

    // Use the New Scaffold
    WavyAuthScaffold(
        title = "Welcome Back",
        subtitle = "Login to tend your forest",
        screenState = screenState,
        onGoToDashboard = { navigateToDashboard() }
    ) {
        // --- Form Content ---
        Box(
            modifier = Modifier
                .background(MintAccent.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                .border(1.dp, MintAccent.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            Text(
                "Login / SignUp",
                color = MintAccent,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelLarge
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        WavyTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = "Email Address",
            icon = Icons.Default.Email,
            keyboardType = KeyboardType.Email
        )

        Spacer(modifier = Modifier.height(16.dp))

        WavyTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = "Password",
            icon = Icons.Default.Lock,
            isPassword = true,
            imeAction = ImeAction.Done,
            onAction = { viewModel.login(email, password) }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Standard Login Button (Visible during Input/Loading/Fail)
        ForestButton(
            text = "Login",
            onClick = { viewModel.login(email, password) },
            isLoading = screenState == AuthScreenState.Loading,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Hide "Sign Up" link if we are loading or succeeding to reduce clutter
        AnimatedVisibility(visible = screenState == AuthScreenState.Input || screenState == AuthScreenState.Failed) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Don't have an account?", color = Color.White.copy(alpha = 0.7f))
                TextButton(onClick = {
                    viewModel.resetState()
                    navController.navigate(NavigationItem.SignUpScreen){
                        popUpTo(0){
                            inclusive = true
                        }
                    }
                }) {
                    Text("Sign Up", color = MintAccent, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}