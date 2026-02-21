package com.example.taskmanager.presentation.task.create

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.taskmanager.domain.model.TaskStatus
import com.example.taskmanager.presentation.ui.components.AppTextField
import com.example.taskmanager.presentation.ui.components.ErrorBanner
import com.example.taskmanager.presentation.ui.components.FullScreenLoading
import com.example.taskmanager.presentation.ui.components.PrimaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskScreen(
    navController: NavHostController,
    editTaskId: Int? = null,
    viewModel: CreateTaskViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Load task if edit mode
    LaunchedEffect(editTaskId) {
        editTaskId?.let { viewModel.loadTask(it) }
    }

    // Navigate back on success
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) navController.popBackStack()
    }

    if (state.isLoading) {
        FullScreenLoading()
        return
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        // Header gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primary.copy(0.7f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Spacer(Modifier.height(32.dp))

            // Top bar
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                ) {
                    Icon(Icons.Default.ArrowBack, "Back")
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    if (state.isEditMode) "Edit Task" else "Create Task",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(Modifier.height(20.dp))

            // Form Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {

                    Text(
                        if (state.isEditMode) "Update your task" else "What needs to be done?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(16.dp))

                    // Title
                    AppTextField(
                        value = state.title,
                        onValueChange = viewModel::onTitleChange,
                        label = "Task Title *",
                        leadingIcon = Icons.Default.Title,
                        placeholder = "e.g. Complete API integration"
                    )
                    Spacer(Modifier.height(12.dp))

                    // Description
                    AppTextField(
                        value = state.description,
                        onValueChange = viewModel::onDescriptionChange,
                        label = "Description (optional)",
                        leadingIcon = Icons.Default.Notes,
                        placeholder = "Add more details...",
                        singleLine = false,
                        maxLines = 4
                    )
                    Spacer(Modifier.height(12.dp))

                    // Due Date
                    AppTextField(
                        value = state.dueDate,
                        onValueChange = viewModel::onDueDateChange,
                        label = "Due Date (optional)",
                        leadingIcon = Icons.Default.CalendarToday,
                        placeholder = "YYYY-MM-DD"
                    )

                    // Status (only in edit mode)
                    if (state.isEditMode) {
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "Status",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface.copy(0.7f)
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TaskStatus.entries.forEach { status ->
                                FilterChip(
                                    selected = state.status == status,
                                    onClick = { viewModel.onStatusChange(status) },
                                    label = { Text(status.displayName) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    ErrorBanner(message = state.error)
                    Spacer(Modifier.height(8.dp))

                    PrimaryButton(
                        text = if (state.isEditMode) "Update Task" else "Create Task",
                        onClick = viewModel::save,
                        isLoading = state.isSaving
                    )
                }
            }

            // Tips Card
            if (!state.isEditMode) {
                Spacer(Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(0.4f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.Lightbulb, null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp).padding(top = 2.dp)
                        )
                        Text(
                            "Tip: Tasks created without internet access will be saved locally and synced when you reconnect.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(0.7f)
                        )
                    }
                }
            }
        }
    }
}