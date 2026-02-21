package com.example.taskmanager.domain.repository

import com.example.taskmanager.domain.model.*
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): Resource<Unit>
    suspend fun register(name: String, email: String, password: String): Resource<Unit>
    suspend fun logout(): Resource<Unit>
    suspend fun getProfile(): Resource<User>
    suspend fun updateProfile(name: String, email: String): Resource<String>
    fun getLocalUser(): Flow<User?>
}

interface TaskRepository {
    fun getTasksFlow(): Flow<List<Task>>
    fun getTasksByStatusFlow(status: TaskStatus): Flow<List<Task>>
    suspend fun getTask(id: Int): Resource<Task>
    suspend fun createTask(title: String, description: String?, dueDate: String?): Resource<Task>
    suspend fun updateTask(id: Int, title: String?, description: String?, status: String?, dueDate: String?): Resource<Unit>
    suspend fun deleteTask(id: Int): Resource<Unit>
    suspend fun syncTasks(): Resource<Unit>
}

interface DashboardRepository {
    fun getDashboardFlow(): Flow<Dashboard?>
    suspend fun fetchDashboard(): Resource<Dashboard>
}