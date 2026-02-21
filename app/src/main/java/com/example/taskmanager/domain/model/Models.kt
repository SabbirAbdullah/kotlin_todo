package com.example.taskmanager.domain.model

import com.example.taskmanager.domain.model.TaskStatus

// ── Task ──────────────────────────────────────────────────────────────────────

data class Task(
    val id: Int,
    val title: String,
    val description: String?,
    val status: TaskStatus,
    val dueDate: String?,
    val isSynced: Boolean = true
)


// ── User ──────────────────────────────────────────────────────────────────────

data class User(
    val id: Int,
    val name: String,
    val email: String
)

// ── Dashboard ─────────────────────────────────────────────────────────────────

data class Dashboard(
    val totalTasks: Int,
    val completedTasks: Int,
    val pendingTasks: Int
) {
    val completionPercent: Float
        get() = if (totalTasks == 0) 0f else completedTasks.toFloat() / totalTasks.toFloat()
}

// ── Resource Wrapper ──────────────────────────────────────────────────────────

sealed class Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val message: String) : Resource<Nothing>()
    object Loading : Resource<Nothing>()
}