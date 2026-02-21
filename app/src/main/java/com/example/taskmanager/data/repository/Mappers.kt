package com.example.taskmanager.data.repository

import com.example.taskmanager.data.local.entity.DashboardEntity
import com.example.taskmanager.data.local.entity.TaskEntity
import com.example.taskmanager.data.local.entity.UserEntity
import com.example.taskmanager.data.remote.dto.DashboardResponse
import com.example.taskmanager.data.remote.dto.TaskDto
import com.example.taskmanager.data.remote.dto.UserDto
import com.example.taskmanager.domain.model.Dashboard
import com.example.taskmanager.domain.model.Task
import com.example.taskmanager.domain.model.TaskStatus
import com.example.taskmanager.domain.model.User

// ── Task Mappers ──────────────────────────────────────────────────────────────

fun TaskDto.toEntity(): TaskEntity = TaskEntity(
    id = id,
    title = title,
    description = description,
    status = status,
    dueDate = dueDate,
    isSynced = true
)

fun TaskEntity.toDomain(): Task = Task(
    id = id,
    title = title,
    description = description,
    status = TaskStatus.fromValue(status),
    dueDate = dueDate,
    isSynced = isSynced
)

fun TaskDto.toDomain(): Task = Task(
    id = id,
    title = title,
    description = description,
    status = TaskStatus.fromValue(status),
    dueDate = dueDate,
    isSynced = true
)

// ── User Mappers ──────────────────────────────────────────────────────────────

fun UserDto.toEntity(): UserEntity = UserEntity(id = id, name = name, email = email)

fun UserDto.toDomain(): User = User(id = id, name = name, email = email)

fun UserEntity.toDomain(): User = User(id = id, name = name, email = email)

// ── Dashboard Mappers ─────────────────────────────────────────────────────────

fun DashboardResponse.toEntity(): DashboardEntity = DashboardEntity(
    totalTasks = totalTasks,
    completedTasks = completedTasks,
    pendingTasks = pendingTasks
)

fun DashboardResponse.toDomain(): Dashboard = Dashboard(
    totalTasks = totalTasks,
    completedTasks = completedTasks,
    pendingTasks = pendingTasks
)

fun DashboardEntity.toDomain(): Dashboard = Dashboard(
    totalTasks = totalTasks,
    completedTasks = completedTasks,
    pendingTasks = pendingTasks
)