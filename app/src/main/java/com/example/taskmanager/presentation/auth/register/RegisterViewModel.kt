package com.example.taskmanager.presentation.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.domain.model.Resource
import com.example.taskmanager.domain.usecase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val passwordVisible: Boolean = false,
    val confirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val error: String = "",
    val isSuccess: Boolean = false
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onNameChange(v: String) { _uiState.value = _uiState.value.copy(name = v, error = "") }
    fun onEmailChange(v: String) { _uiState.value = _uiState.value.copy(email = v, error = "") }
    fun onPasswordChange(v: String) { _uiState.value = _uiState.value.copy(password = v, error = "") }
    fun onConfirmPasswordChange(v: String) { _uiState.value = _uiState.value.copy(confirmPassword = v, error = "") }
    fun togglePasswordVisible() { _uiState.value = _uiState.value.copy(passwordVisible = !_uiState.value.passwordVisible) }
    fun toggleConfirmPasswordVisible() { _uiState.value = _uiState.value.copy(confirmPasswordVisible = !_uiState.value.confirmPasswordVisible) }

    fun register() {
        val state = _uiState.value
        if (state.password != state.confirmPassword) {
            _uiState.value = state.copy(error = "Passwords do not match")
            return
        }
        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, error = "")
            when (val result = registerUseCase(state.name, state.email, state.password)) {
                is Resource.Success -> _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true)
                is Resource.Error -> _uiState.value = _uiState.value.copy(isLoading = false, error = result.message)
                is Resource.Loading -> Unit
            }
        }
    }
}