package com.example.taskmanager.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.domain.model.Dashboard
import com.example.taskmanager.domain.model.Resource
import com.example.taskmanager.domain.model.User
import com.example.taskmanager.domain.usecase.GetDashboardUseCase
import com.example.taskmanager.domain.usecase.GetProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val dashboard: Dashboard? = null,
    val user: User? = null,
    val isLoading: Boolean = true,
    val error: String = "",
    val isSyncing: Boolean = false
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getDashboardUseCase: GetDashboardUseCase,
    private val getProfileUseCase: GetProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        observeLocalData()
        fetchFromNetwork()
    }

    private fun observeLocalData() {
        viewModelScope.launch {
            getDashboardUseCase().collect { dashboard ->
                _uiState.value = _uiState.value.copy(dashboard = dashboard)
            }
        }
        viewModelScope.launch {
            getProfileUseCase.local().collect { user ->
                _uiState.value = _uiState.value.copy(user = user)
            }
        }
    }

    private fun fetchFromNetwork() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            getDashboardUseCase.fetch()
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSyncing = true, error = "")
            when (val result = getDashboardUseCase.fetch()) {
                is Resource.Error -> _uiState.value = _uiState.value.copy(error = result.message)
                else -> Unit
            }
            _uiState.value = _uiState.value.copy(isSyncing = false)
        }
    }
}