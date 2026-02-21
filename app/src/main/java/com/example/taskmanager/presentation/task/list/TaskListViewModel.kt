package com.example.taskmanager.presentation.task.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.domain.model.Resource
import com.example.taskmanager.domain.model.Task
import com.example.taskmanager.domain.model.TaskStatus
import com.example.taskmanager.domain.usecase.DeleteTaskUseCase
import com.example.taskmanager.domain.usecase.GetTasksUseCase
import com.example.taskmanager.domain.usecase.SyncTasksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TaskListUiState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = true,
    val isSyncing: Boolean = false,
    val error: String = "",
    val selectedFilter: FilterOption = FilterOption.ALL,
    val deleteConfirmId: Int? = null
)

enum class FilterOption(val label: String) {
    ALL("All"),
    PENDING("Pending"),
    COMPLETED("Completed")
}

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val syncTasksUseCase: SyncTasksUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskListUiState())
    val uiState: StateFlow<TaskListUiState> = _uiState.asStateFlow()

    private val _filterOption = MutableStateFlow(FilterOption.ALL)

    init {
        observeTasks()
        syncOnStart()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeTasks() {
        viewModelScope.launch {
            _filterOption.flatMapLatest { filter ->
                when (filter) {
                    FilterOption.ALL -> getTasksUseCase()
                    FilterOption.PENDING -> getTasksUseCase.byStatus(TaskStatus.PENDING)
                    FilterOption.COMPLETED -> getTasksUseCase.byStatus(TaskStatus.COMPLETED)
                }
            }.collect { tasks ->
                _uiState.value = _uiState.value.copy(tasks = tasks, isLoading = false)
            }
        }
    }

    private fun syncOnStart() {
        viewModelScope.launch {
            syncTasksUseCase()
        }
    }

    fun setFilter(filter: FilterOption) {
        _filterOption.value = filter
        _uiState.value = _uiState.value.copy(selectedFilter = filter)
    }

    fun deleteTask(id: Int) {
        viewModelScope.launch {
            when (val result = deleteTaskUseCase(id)) {
                is Resource.Error -> _uiState.value = _uiState.value.copy(error = result.message)
                else -> _uiState.value = _uiState.value.copy(deleteConfirmId = null)
            }
        }
    }

    fun showDeleteConfirm(id: Int) {
        _uiState.value = _uiState.value.copy(deleteConfirmId = id)
    }

    fun dismissDeleteConfirm() {
        _uiState.value = _uiState.value.copy(deleteConfirmId = null)
    }

    fun sync() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSyncing = true, error = "")
            when (val result = syncTasksUseCase()) {
                is Resource.Error -> _uiState.value = _uiState.value.copy(error = result.message)
                else -> Unit
            }
            _uiState.value = _uiState.value.copy(isSyncing = false)
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = "")
    }
}