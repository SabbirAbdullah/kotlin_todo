package com.example.taskmanager.presentation.task.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.taskmanager.domain.model.TaskStatus
import com.example.taskmanager.presentation.navigation.Screen
import com.example.taskmanager.presentation.ui.components.FullScreenLoading
import com.example.taskmanager.presentation.ui.components.StatusBadge
import com.example.taskmanager.presentation.ui.theme.CompletedGreen
import com.example.taskmanager.presentation.ui.theme.DeleteRed
import com.example.taskmanager.presentation.ui.theme.PendingOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    navController: NavHostController,
    taskId: Int,
    viewModel: TaskDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(taskId) { viewModel.loadTask(taskId) }
    LaunchedEffect(state.isDeleted) {
        if (state.isDeleted) navController.popBackStack()
    }

    if (state.isLoading) { FullScreenLoading(); return }

    if (state.showDeleteDialog) {
        AlertDialog(
            onDismissRequest = viewModel::hideDeleteDialog,
            icon = { Icon(Icons.Default.DeleteForever, null, tint = DeleteRed) },
            title = { Text("Delete Task") },
            text = { Text("This task will be permanently deleted.") },
            confirmButton = {
                TextButton(onClick = viewModel::delete) {
                    Text("Delete", color = DeleteRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = { TextButton(onClick = viewModel::hideDeleteDialog) { Text("Cancel") } }
        )
    }

    val task = state.task ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Task Detail", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.EditTask.createRoute(taskId)) }) {
                        Icon(Icons.Default.Edit, "Edit", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = viewModel::showDeleteDialog) {
                        Icon(Icons.Default.Delete, "Delete", tint = DeleteRed)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Card with gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary.copy(0.15f),
                                Color.Transparent
                            )
                        )
                    )
                    .padding(24.dp)
            ) {
                Column {
                    StatusBadge(status = task.status)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        task.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    if (!task.isSynced) {
                        Spacer(Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.CloudOff, null, modifier = Modifier.size(14.dp), tint = PendingOrange)
                            Text("Not synced", style = MaterialTheme.typography.labelSmall, color = PendingOrange)
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                // Description
                if (!task.description.isNullOrBlank()) {
                    DetailSection(
                        icon = Icons.Default.Notes,
                        title = "Description",
                        content = task.description
                    )
                    Spacer(Modifier.height(12.dp))
                }

                // Due Date
                task.dueDate?.let { date ->
                    DetailSection(
                        icon = Icons.Default.CalendarToday,
                        title = "Due Date",
                        content = date
                    )
                    Spacer(Modifier.height(12.dp))
                }

                // Status Detail
                DetailSection(
                    icon = when (task.status) {
                        TaskStatus.PENDING -> Icons.Default.HourglassEmpty
                        TaskStatus.COMPLETED -> Icons.Default.CheckCircle
                    },
                    title = "Status",
                    content = task.status.displayName,
                    contentColor = when (task.status) {
                        TaskStatus.PENDING -> PendingOrange
                        TaskStatus.COMPLETED -> CompletedGreen
                    }
                )

                Spacer(Modifier.height(32.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = viewModel::showDeleteDialog,
                        modifier = Modifier.weight(1f).height(50.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = DeleteRed),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DeleteRed)
                    ) {
                        Icon(Icons.Default.Delete, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Delete")
                    }

                    Button(
                        onClick = { navController.navigate(Screen.EditTask.createRoute(taskId)) },
                        modifier = Modifier.weight(1f).height(50.dp)
                    ) {
                        Icon(Icons.Default.Edit, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Edit")
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailSection(
    icon: ImageVector,
    title: String,
    content: String,
    contentColor: Color? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top,
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
                Text(
                    title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(0.5f),
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor ?: MaterialTheme.colorScheme.onSurface,
                    fontWeight = if (contentColor != null) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}