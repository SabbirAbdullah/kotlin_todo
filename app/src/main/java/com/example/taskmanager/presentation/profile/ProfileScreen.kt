package com.example.taskmanager.presentation.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.taskmanager.presentation.dashboard.BottomNavBar
import com.example.taskmanager.presentation.navigation.Screen
import com.example.taskmanager.presentation.ui.components.AppTextField
import com.example.taskmanager.presentation.ui.components.ErrorBanner
import com.example.taskmanager.presentation.ui.components.PrimaryButton
import com.example.taskmanager.presentation.ui.theme.CompletedGreen
import com.example.taskmanager.presentation.ui.theme.DeleteRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.isLoggedOut) {
        if (state.isLoggedOut) {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = viewModel::toggleEditMode) {
                        Icon(
                            if (state.isEditMode) Icons.Default.Close else Icons.Default.Edit,
                            if (state.isEditMode) "Cancel edit" else "Edit profile"
                        )
                    }
                }
            )
        },
        bottomBar = { BottomNavBar(navController, Screen.Profile.route) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            // ── Avatar Header ─────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primary.copy(0.7f)
                            )
                        )
                    )
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Avatar circle with initials
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.user?.name?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                            style = MaterialTheme.typography.headlineLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 36.sp
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        state.user?.name ?: "Loading...",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        state.user?.email ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(0.8f)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Success Banner ────────────────────────────────────────────────
            AnimatedVisibility(visible = state.successMessage.isNotBlank()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = CompletedGreen.copy(0.12f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, null, tint = CompletedGreen, modifier = Modifier.size(18.dp))
                        Text(state.successMessage, style = MaterialTheme.typography.bodySmall, color = CompletedGreen)
                    }
                }
            }

            ErrorBanner(message = state.error, modifier = Modifier.padding(horizontal = 16.dp))

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                // ── Profile Info Card ─────────────────────────────────────────
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "Account Information",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(16.dp))

                        if (state.isEditMode) {
                            AppTextField(
                                value = state.editName,
                                onValueChange = viewModel::onNameChange,
                                label = "Full Name",
                                leadingIcon = Icons.Default.Person
                            )
                            Spacer(Modifier.height(12.dp))
                            AppTextField(
                                value = state.editEmail,
                                onValueChange = viewModel::onEmailChange,
                                label = "Email",
                                leadingIcon = Icons.Default.Email,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                            )
                            Spacer(Modifier.height(16.dp))
                            PrimaryButton(
                                text = "Save Changes",
                                onClick = viewModel::saveProfile,
                                isLoading = state.isSaving
                            )
                        } else {
                            ProfileInfoRow(icon = Icons.Default.Person, label = "Name", value = state.user?.name ?: "")
                            Divider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline.copy(0.3f))
                            ProfileInfoRow(icon = Icons.Default.Email, label = "Email", value = state.user?.email ?: "")
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // ── Logout Button ─────────────────────────────────────────────
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "Session",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = viewModel::logout,
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DeleteRed),
                            shape = RoundedCornerShape(14.dp),
                            enabled = !state.isLoggingOut
                        ) {
                            if (state.isLoggingOut) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(Icons.Default.Logout, null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Sign Out", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        }
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(0.5f))
            Text(value.ifBlank { "—" }, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
        }
    }
}