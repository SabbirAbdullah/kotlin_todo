package com.example.taskmanager.presentation.task.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.domain.model.Resource
import com.example.taskmanager.domain.model.TaskStatus
import com.example.taskmanager.domain.usecase.CreateTaskUseCase
import com.example.taskmanager.domain.usecase.GetTaskUseCase
import com.example.taskmanager.domain.usecase.UpdateTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CreateTaskUiState(
    val title: String = "",
    val description: String = "",
    val dueDate: String = "",
    val status: TaskStatus = TaskStatus.PENDING,
    val isEditMode: Boolean = false,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String = "",
    val isSuccess: Boolean = false
)

@HiltViewModel
class CreateTaskViewModel @Inject constructor(
    private val createTaskUseCase: CreateTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val getTaskUseCase: GetTaskUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateTaskUiState())
    val uiState: StateFlow<CreateTaskUiState> = _uiState.asStateFlow()

    private var editTaskId: Int? = null

    fun loadTask(id: Int) {
        editTaskId = id
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, isEditMode = true)
            when (val result = getTaskUseCase(id)) {
                is Resource.Success -> {
                    val task = result.data
                    _uiState.value = _uiState.value.copy(
                        title = task.title,
                        description = task.description ?: "",
                        dueDate = task.dueDate ?: "",
                        status = task.status,
                        isLoading = false
                    )
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = result.message)
                }
                else -> Unit
            }
        }
    }

    fun onTitleChange(v: String) { _uiState.value = _uiState.value.copy(title = v, error = "") }
    fun onDescriptionChange(v: String) { _uiState.value = _uiState.value.copy(description = v) }
    fun onDueDateChange(v: String) { _uiState.value = _uiState.value.copy(dueDate = v) }
    fun onStatusChange(v: TaskStatus) { _uiState.value = _uiState.value.copy(status = v) }

    fun save() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.value = state.copy(isSaving = true, error = "")

            val result = if (state.isEditMode && editTaskId != null) {
                updateTaskUseCase(
                    id = editTaskId!!,
                    title = state.title,
                    description = state.description.ifBlank { null },
                    status = state.status.value,
                    dueDate = state.dueDate.ifBlank { null }
                )
            } else {
                createTaskUseCase(
                    title = state.title,
                    description = state.description.ifBlank { null },
                    dueDate = state.dueDate.ifBlank { null }
                )
            }

            when (result) {
                is Resource.Success -> _uiState.value = _uiState.value.copy(isSaving = false, isSuccess = true)
                is Resource.Error -> _uiState.value = _uiState.value.copy(isSaving = false, error = result.message)
                else -> Unit
            }
        }
    }
}