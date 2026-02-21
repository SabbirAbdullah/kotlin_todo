package com.example.taskmanager.presentation.task.list

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.taskmanager.domain.model.Task
import com.example.taskmanager.domain.model.TaskStatus
import com.example.taskmanager.presentation.dashboard.BottomNavBar
import com.example.taskmanager.presentation.navigation.Screen
import com.example.taskmanager.presentation.ui.components.EmptyState
import com.example.taskmanager.presentation.ui.components.StatusBadge
import com.example.taskmanager.presentation.ui.theme.CompletedGreen
import com.example.taskmanager.presentation.ui.theme.DeleteRed
import com.example.taskmanager.presentation.ui.theme.PendingOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    navController: NavHostController,
    viewModel: TaskListViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Delete confirmation dialog
    state.deleteConfirmId?.let { taskId ->
        AlertDialog(
            onDismissRequest = viewModel::dismissDeleteConfirm,
            icon = { Icon(Icons.Default.DeleteForever, null, tint = DeleteRed) },
            title = { Text("Delete Task") },
            text = { Text("Are you sure you want to delete this task? This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = { viewModel.deleteTask(taskId) }) {
                    Text("Delete", color = DeleteRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissDeleteConfirm) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tasks", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = viewModel::sync) {
                        if (state.isSyncing) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Sync, "Sync")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.CreateTask.route) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, "Add", tint = Color.White)
            }
        },
        bottomBar = { BottomNavBar(navController, Screen.TaskList.route) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // ── Error Banner ─────────────────────────────────────────────────
            AnimatedVisibility(visible = state.error.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = DeleteRed.copy(0.1f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.ErrorOutline, null, tint = DeleteRed, modifier = Modifier.size(18.dp))
                        Text(state.error, style = MaterialTheme.typography.bodySmall, color = DeleteRed, modifier = Modifier.weight(1f))
                        IconButton(onClick = viewModel::clearError, modifier = Modifier.size(20.dp)) {
                            Icon(Icons.Default.Close, null, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }

            // ── Filter Chips ──────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterOption.entries.forEach { filter ->
                    FilterChip(
                        selected = state.selectedFilter == filter,
                        onClick = { viewModel.setFilter(filter) },
                        label = {
                            Text(filter.label, style = MaterialTheme.typography.labelMedium)
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
                Spacer(Modifier.weight(1f))
                Text(
                    "${state.tasks.size} tasks",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(0.5f),
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

            // ── Task List ─────────────────────────────────────────────────────
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (state.tasks.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.AssignmentTurnedIn,
                    title = "No tasks here",
                    subtitle = "Tap the + button to add your first task",
                    modifier = Modifier.fillMaxSize().padding(top = 60.dp)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        items = state.tasks,
                        key = { it.id }
                    ) { task ->
                        TaskItemCard(
                            task = task,
                            onEdit = { navController.navigate(Screen.EditTask.createRoute(task.id)) },
                            onDelete = { viewModel.showDeleteConfirm(task.id) },
                            onClick = { navController.navigate(Screen.TaskDetail.createRoute(task.id)) }
                        )
                    }
                    item { Spacer(Modifier.height(80.dp)) } // FAB space
                }
            }
        }
    }
}

@Composable
fun TaskItemCard(
    task: Task,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val statusColor = when (task.status) {
        TaskStatus.PENDING -> PendingOrange
        TaskStatus.COMPLETED -> CompletedGreen
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            // Left status bar
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(56.dp)
                    .padding(vertical = 2.dp)
            ) {
                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRoundRect(
                        color = statusColor,
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f)
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textDecoration = if (task.status == TaskStatus.COMPLETED) TextDecoration.LineThrough else null,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(8.dp))
                    StatusBadge(status = task.status)
                }

                task.description?.takeIf { it.isNotBlank() }?.let { desc ->
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = desc,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.6f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        task.dueDate?.let { date ->
                            Icon(
                                Icons.Default.CalendarToday, null,
                                modifier = Modifier.size(12.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(date, style = MaterialTheme.typography.labelSmall)
                        }
                        if (!task.isSynced) {
                            Spacer(Modifier.width(4.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = PendingOrange.copy(0.15f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Icon(Icons.Default.CloudOff, null, modifier = Modifier.size(10.dp), tint = PendingOrange)
                                    Text("Offline", style = MaterialTheme.typography.labelSmall, color = PendingOrange)
                                }
                            }
                        }
                    }

                    Row {
                        IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                            Icon(
                                Icons.Default.EditNote, "Edit",
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                            Icon(
                                Icons.Default.Delete, "Delete",
                                modifier = Modifier.size(18.dp),
                                tint = DeleteRed
                            )
                        }
                    }
                }
            }
        }
    }
}