package com.example.taskmanager.presentation.task.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.domain.model.Resource
import com.example.taskmanager.domain.model.Task
import com.example.taskmanager.domain.usecase.DeleteTaskUseCase
import com.example.taskmanager.domain.usecase.GetTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TaskDetailUiState(
    val task: Task? = null,
    val isLoading: Boolean = true,
    val error: String = "",
    val isDeleted: Boolean = false,
    val showDeleteDialog: Boolean = false
)

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val getTaskUseCase: GetTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskDetailUiState())
    val uiState: StateFlow<TaskDetailUiState> = _uiState.asStateFlow()

    fun loadTask(id: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = getTaskUseCase(id)) {
                is Resource.Success -> _uiState.value = _uiState.value.copy(task = result.data, isLoading = false)
                is Resource.Error -> _uiState.value = _uiState.value.copy(error = result.message, isLoading = false)
                else -> Unit
            }
        }
    }

    fun showDeleteDialog() { _uiState.value = _uiState.value.copy(showDeleteDialog = true) }
    fun hideDeleteDialog() { _uiState.value = _uiState.value.copy(showDeleteDialog = false) }

    fun delete() {
        val id = _uiState.value.task?.id ?: return
        viewModelScope.launch {
            when (val result = deleteTaskUseCase(id)) {
                is Resource.Success -> _uiState.value = _uiState.value.copy(isDeleted = true, showDeleteDialog = false)
                is Resource.Error -> _uiState.value = _uiState.value.copy(error = result.message, showDeleteDialog = false)
                else -> Unit
            }
        }
    }
}