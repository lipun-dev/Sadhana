package com.example.pomodora.view.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pomodora.model.ResultState
import com.example.pomodora.view.NavigationItem
import com.example.pomodora.view.utils.PomoEmailField
import com.example.pomodora.view.utils.PomoPasswordField
import com.example.pomodora.viewModel.AuthViewModel

@Composable
fun SignUpScreen(
    navController: NavController,
    viewModel: AuthViewModel
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    val authState by viewModel.authState.collectAsState()
    val context = LocalContext.current

    val isPasswordValid = password.length >= 8
    val isFormValid = email.isNotBlank() && isPasswordValid

    LaunchedEffect(authState) {
        when (authState) {
            is ResultState.Success -> {
                Toast.makeText(context, "Account Created!", Toast.LENGTH_SHORT).show()
                navController.navigate(NavigationItem.Dashboard) {
                    popUpTo(NavigationItem.SignUpScreen) { inclusive = true }
                }
            }
            is ResultState.Error -> {
                val msg = (authState as ResultState.Error).message
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Create Account",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Start your planting journey",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            PomoEmailField(value = email, onValueChange = { email = it })

            Spacer(modifier = Modifier.height(16.dp))

            PomoPasswordField(
                value = password,
                onValueChange = { password = it },
                label = "Choose Password",
                validateLength = true, // Strict validation for new users
                onAction = { if (isFormValid) viewModel.signUp(email, password) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.signUp(email, password) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                // Disable button if form is invalid or loading
                enabled = authState !is ResultState.Loading && isFormValid
            ) {
                if (authState is ResultState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Register", style = MaterialTheme.typography.titleMedium)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = {
                    viewModel.resetState()
                    navController.popBackStack()
                }
            ) {
                Text("Already have an account? Login")
            }
        }
    }
}