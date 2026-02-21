package com.example.taskmanager.presentation.dashboard

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.taskmanager.presentation.navigation.Screen
import com.example.taskmanager.presentation.ui.components.GradientCard
import com.example.taskmanager.presentation.ui.components.StatCard
import com.example.taskmanager.presentation.ui.theme.CompletedGreen
import com.example.taskmanager.presentation.ui.theme.PendingOrange
import com.example.taskmanager.presentation.ui.theme.Purple
import androidx.compose.material3.MaterialTheme



@Composable
fun DashboardScreen(
    navController: NavHostController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = { BottomNavBar(navController = navController, currentRoute = Screen.Dashboard.route) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background)
        ) {
            // ── Header ────────────────────────────────────────────────────────
            GradientCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Hello, ${state.user?.name?.split(" ")?.firstOrNull() ?: "there"} 👋",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            "Let's get things done today",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(0.8f)
                        )
                    }
                    IconButton(onClick = viewModel::refresh) {
                        Icon(
                            if (state.isSyncing) Icons.Default.HourglassEmpty
                            else Icons.Default.Refresh,
                            null,
                            tint = Color.White
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Progress ring area
                val dashboard = state.dashboard
                if (dashboard != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Circular progress
                        Box(
                            modifier = Modifier.size(90.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                progress = { dashboard.completionPercent },
                                modifier = Modifier.fillMaxSize(),
                                color = Color.White,
                                trackColor = Color.White.copy(0.3f),
                                strokeWidth = 8.dp,
                                strokeCap = StrokeCap.Round
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "${(dashboard.completionPercent * 100).toInt()}%",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text("Done", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(0.8f))
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            QuickStat("Total", dashboard.totalTasks.toString(), Color.White)
                            QuickStat("Pending", dashboard.pendingTasks.toString(), Color(0xFFFFD600))
                            QuickStat("Done", dashboard.completedTasks.toString(), Color(0xFF69F0AE))
                        }
                    }
                } else if (state.isLoading) {
                    Box(modifier = Modifier.fillMaxWidth().height(90.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.White, strokeWidth = 3.dp)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Stat Cards ────────────────────────────────────────────────────
            state.dashboard?.let { d ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        label = "Total Tasks",
                        value = d.totalTasks.toString(),
                        icon = Icons.Default.Assignment,
                        color = Purple,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = "Pending",
                        value = d.pendingTasks.toString(),
                        icon = Icons.Default.HourglassEmpty,
                        color = PendingOrange,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = "Completed",
                        value = d.completedTasks.toString(),
                        icon = Icons.Default.CheckCircle,
                        color = CompletedGreen,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Quick Actions ─────────────────────────────────────────────────
            Text(
                "Quick Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    icon = Icons.Default.Add,
                    label = "New Task",
                    color = MaterialTheme.colorScheme.primary,
                    onClick = { navController.navigate(Screen.CreateTask.route) },
                    modifier = Modifier.weight(1f)
                )
                QuickActionCard(
                    icon = Icons.Default.List,
                    label = "All Tasks",
                    color = CompletedGreen,
                    onClick = { navController.navigate(Screen.TaskList.route) },
                    modifier = Modifier.weight(1f)
                )
                QuickActionCard(
                    icon = Icons.Default.Person,
                    label = "Profile",
                    color = PendingOrange,
                    onClick = { navController.navigate(Screen.Profile.route) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun QuickStat(label: String, value: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
        Text("$label: ", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(0.8f))
        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
private fun QuickActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(28.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.SemiBold)
        }
    }
}

// ── Bottom Navigation Bar ─────────────────────────────────────────────────────

@Composable
fun BottomNavBar(navController: NavHostController, currentRoute: String) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            Triple(Screen.Dashboard.route, Icons.Default.Dashboard, "Dashboard"),
            Triple(Screen.TaskList.route, Icons.Default.Assignment, "Tasks"),
            Triple(Screen.CreateTask.route, Icons.Default.Add, "Create"),
            Triple(Screen.Profile.route, Icons.Default.Person, "Profile")
        )
        items.forEach { (route, icon, label) ->
            NavigationBarItem(
                selected = currentRoute == route,
                onClick = {
                    if (currentRoute != route) {
                        navController.navigate(route) {
                            popUpTo(Screen.Dashboard.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(icon, label) },
                label = { Text(label) }
            )
        }
    }
}