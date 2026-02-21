package com.example.taskmanager.presentation.auth.register

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
import com.example.taskmanager.domain.usecase.RegisterUseCase

@Composable
fun RegisterScreen(
    navController: NavHostController,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.Register.route) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp))
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
            Spacer(Modifier.height(40.dp))

            // Back + Title Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                ) {
                    Icon(Icons.Default.ArrowBack, "Back")
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    "Create Account",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Join us today!", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(
                        "Create your account to get started",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.6f)
                    )
                    Spacer(Modifier.height(20.dp))

                    AppTextField(
                        value = state.name,
                        onValueChange = viewModel::onNameChange,
                        label = "Full Name",
                        leadingIcon = Icons.Default.Person,
                        placeholder = "Sabbir Ahmed"
                    )
                    Spacer(Modifier.height(12.dp))

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
                    Spacer(Modifier.height(12.dp))

                    AppTextField(
                        value = state.confirmPassword,
                        onValueChange = viewModel::onConfirmPasswordChange,
                        label = "Confirm Password",
                        leadingIcon = Icons.Default.LockOpen,
                        visualTransformation = if (state.confirmPasswordVisible)
                            VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingContent = {
                            IconButton(onClick = viewModel::toggleConfirmPasswordVisible) {
                                Icon(
                                    if (state.confirmPasswordVisible) Icons.Default.VisibilityOff
                                    else Icons.Default.Visibility, null
                                )
                            }
                        }
                    )

                    Spacer(Modifier.height(12.dp))
                    ErrorBanner(message = state.error)
                    Spacer(Modifier.height(8.dp))

                    PrimaryButton(
                        text = "Create Account",
                        onClick = viewModel::register,
                        isLoading = state.isLoading
                    )

                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Already have an account?", style = MaterialTheme.typography.bodyMedium)
                        TextButton(onClick = { navController.navigate(Screen.Login.route) }) {
                            Text("Login", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}