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