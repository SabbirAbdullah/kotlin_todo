package com.example.taskmanager.domain.usecase

import com.example.taskmanager.domain.model.*
import com.example.taskmanager.domain.repository.*

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// ── Auth Use Cases ────────────────────────────────────────────────────────────

class LoginUseCase @Inject constructor(private val repo: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Resource<Unit> {
        if (email.isBlank()) return Resource.Error("Email cannot be empty")
        if (password.isBlank()) return Resource.Error("Password cannot be empty")
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return Resource.Error("Invalid email address")
        return repo.login(email.trim(), password)
    }
}

class RegisterUseCase @Inject constructor(private val repo: AuthRepository) {
    suspend operator fun invoke(name: String, email: String, password: String): Resource<Unit> {
        if (name.isBlank()) return Resource.Error("Name cannot be empty")
        if (email.isBlank()) return Resource.Error("Email cannot be empty")
        if (password.isBlank()) return Resource.Error("Password cannot be empty")
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return Resource.Error("Invalid email address")
        if (password.length < 6) return Resource.Error("Password must be at least 6 characters")
        return repo.register(name.trim(), email.trim(), password)
    }
}

class LogoutUseCase @Inject constructor(private val repo: AuthRepository) {
    suspend operator fun invoke(): Resource<Unit> = repo.logout()
}

class GetProfileUseCase @Inject constructor(private val repo: AuthRepository) {
    suspend operator fun invoke(): Resource<User> = repo.getProfile()
    fun local(): Flow<User?> = repo.getLocalUser()
}

class UpdateProfileUseCase @Inject constructor(private val repo: AuthRepository) {
    suspend operator fun invoke(name: String, email: String): Resource<String> {
        if (name.isBlank()) return Resource.Error("Name cannot be empty")
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return Resource.Error("Invalid email address")
        return repo.updateProfile(name.trim(), email.trim())
    }
}

// ── Task Use Cases ────────────────────────────────────────────────────────────

class GetTasksUseCase @Inject constructor(private val repo: TaskRepository) {
    operator fun invoke(): Flow<List<Task>> = repo.getTasksFlow()
    fun byStatus(status: TaskStatus): Flow<List<Task>> = repo.getTasksByStatusFlow(status)
}

class GetTaskUseCase @Inject constructor(private val repo: TaskRepository) {
    suspend operator fun invoke(id: Int): Resource<Task> = repo.getTask(id)
}

class CreateTaskUseCase @Inject constructor(private val repo: TaskRepository) {
    suspend operator fun invoke(title: String, description: String?, dueDate: String?): Resource<Task> {
        if (title.isBlank()) return Resource.Error("Title cannot be empty")
        return repo.createTask(title.trim(), description?.trim(), dueDate)
    }
}

class UpdateTaskUseCase @Inject constructor(private val repo: TaskRepository) {
    suspend operator fun invoke(
        id: Int,
        title: String?,
        description: String?,
        status: String?,
        dueDate: String?
    ): Resource<Unit> = repo.updateTask(id, title, description, status, dueDate)
}

class DeleteTaskUseCase @Inject constructor(private val repo: TaskRepository) {
    suspend operator fun invoke(id: Int): Resource<Unit> = repo.deleteTask(id)
}

class SyncTasksUseCase @Inject constructor(private val repo: TaskRepository) {
    suspend operator fun invoke(): Resource<Unit> = repo.syncTasks()
}

// ── Dashboard Use Cases ───────────────────────────────────────────────────────

class GetDashboardUseCase @Inject constructor(private val repo: DashboardRepository) {
    operator fun invoke(): Flow<Dashboard?> = repo.getDashboardFlow()
    suspend fun fetch(): Resource<Dashboard> = repo.fetchDashboard()
}