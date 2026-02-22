package com.example.taskmanager.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey



@Entity(tableName = "dashboard_cache")
data class DashboardEntity(
    @PrimaryKey val id: Int = 1,
    val totalTasks: Int,
    val completedTasks: Int,
    val pendingTasks: Int,
    val cachedAt: Long = System.currentTimeMillis()
)