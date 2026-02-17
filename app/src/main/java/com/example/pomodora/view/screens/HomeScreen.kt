package com.example.pomodora.view.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pomodora.view.NavigationItem
import com.example.pomodora.viewModel.AuthViewModel

@Composable
fun HomeScreen(navController: NavController,viewModel: AuthViewModel) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(text = "HomeScreen")
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = {
            viewModel.logout()
            navController.navigate(NavigationItem.LoginScreen)
        }) {
            Text(text = "FocusScreen")
        }

    }


}