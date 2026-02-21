package com.example.taskmanager.data.repository

import com.example.taskmanager.data.local.dao.TaskDao
import com.example.taskmanager.data.local.entity.TaskEntity
import com.example.taskmanager.data.remote.api.ApiService
import com.example.taskmanager.data.remote.dto.CreateTaskRequest
import com.example.taskmanager.data.remote.dto.UpdateTaskRequest
import com.example.taskmanager.domain.model.Resource
import com.example.taskmanager.domain.model.Task
import com.example.taskmanager.domain.model.TaskStatus
import com.example.taskmanager.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val taskDao: TaskDao
) : TaskRepository {

    override fun getTasksFlow(): Flow<List<Task>> =
        taskDao.getAllTasksFlow().map { list -> list.map { it.toDomain() } }

    override fun getTasksByStatusFlow(status: TaskStatus): Flow<List<Task>> =
        taskDao.getTasksByStatusFlow(status.value).map { list -> list.map { it.toDomain() } }

    override suspend fun getTask(id: Int): Resource<Task> {
        return try {
            val response = api.getTask(id)
            if (response.isSuccessful && response.body() != null) {
                val dto = response.body()!!
                taskDao.upsertTask(dto.toEntity())
                Resource.Success(dto.toDomain())
            } else {
                val cached = taskDao.getTaskById(id)
                if (cached != null) Resource.Success(cached.toDomain())
                else Resource.Error("Task not found")
            }
        } catch (e: Exception) {
            val cached = taskDao.getTaskById(id)
            if (cached != null) Resource.Success(cached.toDomain())
            else Resource.Error("Network error: ${e.localizedMessage}")
        }
    }

    override suspend fun createTask(
        title: String,
        description: String?,
        dueDate: String?
    ): Resource<Task> {
        return try {
            val response = api.createTask(CreateTaskRequest(title, description, dueDate))
            if (response.isSuccessful && response.body() != null) {
                val dto = response.body()!!
                taskDao.upsertTask(dto.toEntity())
                Resource.Success(dto.toDomain())
            } else {
                // Save offline — generate a temporary negative ID to avoid conflicts
                val tempId = -(System.currentTimeMillis() % Int.MAX_VALUE).toInt()
                val entity = TaskEntity(
                    id = tempId,
                    title = title,
                    description = description,
                    status = TaskStatus.PENDING.value,
                    dueDate = dueDate,
                    isSynced = false
                )
                taskDao.upsertTask(entity)
                Resource.Success(entity.toDomain())
            }
        } catch (e: Exception) {
            val tempId = -(System.currentTimeMillis() % Int.MAX_VALUE).toInt()
            val entity = TaskEntity(
                id = tempId,
                title = title,
                description = description,
                status = TaskStatus.PENDING.value,
                dueDate = dueDate,
                isSynced = false
            )
            taskDao.upsertTask(entity)
            Resource.Success(entity.toDomain())
        }
    }

    override suspend fun updateTask(
        id: Int,
        title: String?,
        description: String?,
        status: String?,
        dueDate: String?
    ): Resource<Unit> {
        return try {
            val response = api.updateTask(id, UpdateTaskRequest(title, description, status, dueDate))
            if (response.isSuccessful) {
                // Refresh local cache
                val cached = taskDao.getTaskById(id)
                cached?.let {
                    taskDao.upsertTask(
                        it.copy(
                            title = title ?: it.title,
                            description = description ?: it.description,
                            status = status ?: it.status,
                            dueDate = dueDate ?: it.dueDate,
                            isSynced = true
                        )
                    )
                }
                Resource.Success(Unit)
            } else {
                Resource.Error("Update failed: ${response.code()}")
            }
        } catch (e: Exception) {
            Resource.Error("Network error: ${e.localizedMessage}")
        }
    }

    override suspend fun deleteTask(id: Int): Resource<Unit> {
        return try {
            val response = api.deleteTask(id)
            if (response.isSuccessful) {
                taskDao.deleteTaskById(id)
                Resource.Success(Unit)
            } else {
                Resource.Error("Delete failed: ${response.code()}")
            }
        } catch (e: Exception) {
            Resource.Error("Network error: ${e.localizedMessage}")
        }
    }

    override suspend fun syncTasks(): Resource<Unit> {
        return try {
            // 1. Push unsynced tasks to server
            val unsynced = taskDao.getUnsyncedTasks()
            for (entity in unsynced) {
                if (entity.id < 0) { // offline-created
                    val response = api.createTask(
                        CreateTaskRequest(entity.title, entity.description, entity.dueDate)
                    )
                    if (response.isSuccessful && response.body() != null) {
                        taskDao.deleteTaskById(entity.id)
                        taskDao.upsertTask(response.body()!!.toEntity())
                    }
                }
            }

            // 2. Pull all tasks fresh from server
            val response = api.getTasks()
            if (response.isSuccessful && response.body() != null) {
                val serverTasks = response.body()!!.tasks
                taskDao.clearAll()
                taskDao.upsertTasks(serverTasks.map { it.toEntity() })
                Resource.Success(Unit)
            } else {
                Resource.Error("Sync failed: ${response.code()}")
            }
        } catch (e: Exception) {
            Resource.Error("Sync error: ${e.localizedMessage}")
        }
    }
}