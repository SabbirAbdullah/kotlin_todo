package com.example.taskmanager.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.domain.model.Resource
import com.example.taskmanager.domain.model.User
import com.example.taskmanager.domain.usecase.LogoutUseCase
import com.example.taskmanager.domain.usecase.GetProfileUseCase
import com.example.taskmanager.domain.usecase.UpdateProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val user: User? = null,
    val editName: String = "",
    val editEmail: String = "",
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isLoggingOut: Boolean = false,
    val isEditMode: Boolean = false,
    val error: String = "",
    val successMessage: String = "",
    val isLoggedOut: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        observeLocalUser()
        fetchProfile()
    }

    private fun observeLocalUser() {
        viewModelScope.launch {
            getProfileUseCase.local().collect { user ->
                user?.let {
                    _uiState.value = _uiState.value.copy(
                        user = it,
                        editName = it.name,
                        editEmail = it.email
                    )
                }
            }
        }
    }

    private fun fetchProfile() {
        viewModelScope.launch {
            when (val result = getProfileUseCase()) {
                is Resource.Success -> _uiState.value = _uiState.value.copy(
                    user = result.data,
                    editName = result.data.name,
                    editEmail = result.data.email,
                    isLoading = false
                )
                is Resource.Error -> _uiState.value = _uiState.value.copy(isLoading = false)
                else -> Unit
            }
        }
    }

    fun onNameChange(v: String) { _uiState.value = _uiState.value.copy(editName = v, error = "") }
    fun onEmailChange(v: String) { _uiState.value = _uiState.value.copy(editEmail = v, error = "") }
    fun toggleEditMode() { _uiState.value = _uiState.value.copy(isEditMode = !_uiState.value.isEditMode, error = "", successMessage = "") }

    fun saveProfile() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.value = state.copy(isSaving = true, error = "")
            when (val result = updateProfileUseCase(state.editName, state.editEmail)) {
                is Resource.Success -> _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    isEditMode = false,
                    successMessage = result.data
                )
                is Resource.Error -> _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = result.message
                )
                else -> Unit
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoggingOut = true)
            logoutUseCase()
            _uiState.value = _uiState.value.copy(isLoggingOut = false, isLoggedOut = true)
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(error = "", successMessage = "")
    }
}