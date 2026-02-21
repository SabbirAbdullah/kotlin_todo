package com.example.taskmanager.presentation.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.domain.model.Resource
import com.example.taskmanager.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val error: String = "",
    val isSuccess: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(v: String) = _uiState.value.let { _uiState.value = it.copy(email = v, error = "") }
    fun onPasswordChange(v: String) = _uiState.value.let { _uiState.value = it.copy(password = v, error = "") }
    fun togglePasswordVisible() = _uiState.value.let { _uiState.value = it.copy(passwordVisible = !it.passwordVisible) }

    fun login() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, error = "")
            when (val result = loginUseCase(state.email, state.password)) {
                is Resource.Success -> _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true)
                is Resource.Error -> _uiState.value = _uiState.value.copy(isLoading = false, error = result.message)
                is Resource.Loading -> Unit
            }
        }
    }
}