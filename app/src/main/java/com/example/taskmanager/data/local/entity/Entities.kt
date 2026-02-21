package com.example.taskmanager.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val description: String?,
    val status: String,        // "pending" | "completed"
    val dueDate: String?,
    val isSynced: Boolean = true,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val email: String
)

@Entity(tableName = "dashboard_cache")
data class DashboardEntity(
    @PrimaryKey val id: Int = 1,
    val totalTasks: Int,
    val completedTasks: Int,
    val pendingTasks: Int,
    val cachedAt: Long = System.currentTimeMillis()
)