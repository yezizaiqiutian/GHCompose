package com.gh.ghcompose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val rememberMe: Boolean = false,
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
)

class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, emailError = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null) }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onRememberMeChange(rememberMe: Boolean) {
        _uiState.update { it.copy(rememberMe = rememberMe) }
    }

    fun onLoginClick(onSuccess: () -> Unit) {
        val email = _uiState.value.email
        val password = _uiState.value.password

        // 验证邮箱
        val emailError = if (email.isBlank()) {
            "邮箱不能为空"
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            "请输入有效的邮箱地址"
        } else {
            null
        }

        // 验证密码
        val passwordError = if (password.isBlank()) {
            "密码不能为空"
        } else if (password.length < 6) {
            "密码至少需要6位"
        } else {
            null
        }

        if (emailError != null || passwordError != null) {
            _uiState.update {
                it.copy(
                    emailError = emailError,
                    passwordError = passwordError
                )
            }
            return
        }

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            // 模拟网络请求
            delay(2000)

            // 这里应该是实际的登录逻辑
            // val result = authRepository.login(email, password)

            _uiState.update { it.copy(isLoading = false) }

            // 假设登录成功
            onSuccess()
        }
    }
}