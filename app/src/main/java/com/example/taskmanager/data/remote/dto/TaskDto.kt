package com.example.taskmanager.data.remote.dto


import com.google.gson.annotations.SerializedName

// ── Request DTOs ──────────────────────────────────────────────────────────────

data class CreateTaskRequest(
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
    @SerializedName("dueDate") val dueDate: String?
)

data class UpdateTaskRequest(
    @SerializedName("title") val title: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("dueDate") val dueDate: String?
)

// ── Response DTOs ─────────────────────────────────────────────────────────────

data class TaskDto(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
    @SerializedName("status") val status: String,
    @SerializedName("dueDate") val dueDate: String?
)

data class TaskListResponse(
    @SerializedName("tasks") val tasks: List<TaskDto>
)

data class DashboardResponse(
    @SerializedName("totalTasks") val totalTasks: Int,
    @SerializedName("completedTasks") val completedTasks: Int,
    @SerializedName("pendingTasks") val pendingTasks: Int
) {
}