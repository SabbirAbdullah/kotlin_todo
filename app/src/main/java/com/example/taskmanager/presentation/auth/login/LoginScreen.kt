package com.example.taskmanager.presentation.auth.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.taskmanager.presentation.navigation.Screen
import com.example.taskmanager.presentation.ui.components.AppTextField
import com.example.taskmanager.presentation.ui.components.ErrorBanner
import com.example.taskmanager.presentation.ui.components.PrimaryButton


@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            navController.navigate(Screen.Dashboard.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top gradient blob
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(bottomStart = 60.dp, bottomEnd = 60.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(48.dp))

            // Logo
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.CheckCircle, null, tint = Color.White, modifier = Modifier.size(48.dp))
            }

            Spacer(Modifier.height(16.dp))
            Text(
                "Task Manager",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                "Stay organized, get things done",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(0.8f)
            )

            Spacer(Modifier.height(40.dp))

            // Login Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        "Welcome back",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Sign in to your account",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.6f)
                    )
                    Spacer(Modifier.height(20.dp))

                    AppTextField(
                        value = state.email,
                        onValueChange = viewModel::onEmailChange,
                        label = "Email",
                        leadingIcon = Icons.Default.Email,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        placeholder = "sabbir@gmail.com"
                    )
                    Spacer(Modifier.height(12.dp))

                    AppTextField(
                        value = state.password,
                        onValueChange = viewModel::onPasswordChange,
                        label = "Password",
                        leadingIcon = Icons.Default.Lock,
                        visualTransformation = if (state.passwordVisible)
                            VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingContent = {
                            IconButton(onClick = viewModel::togglePasswordVisible) {
                                Icon(
                                    if (state.passwordVisible) Icons.Default.VisibilityOff
                                    else Icons.Default.Visibility, null
                                )
                            }
                        }
                    )

                    TextButton(
                        onClick = { navController.navigate(Screen.ForgotPassword.route) },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Forgot Password?", style = MaterialTheme.typography.labelLarge)
                    }

                    ErrorBanner(message = state.error)
                    Spacer(Modifier.height(8.dp))

                    PrimaryButton(
                        text = "Login",
                        onClick = viewModel::login,
                        isLoading = state.isLoading
                    )

                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Don't have an account?", style = MaterialTheme.typography.bodyMedium)
                        TextButton(onClick = { navController.navigate(Screen.Register.route) }) {
                            Text("Register", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}