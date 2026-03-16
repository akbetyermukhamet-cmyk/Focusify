/*
 * Copyright (c) 2025-2026 Akbet Ereke
 *
 * This file is part of Tomato - a minimalist pomodoro timer for Android.
 *
 * Tomato is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Tomato is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Tomato.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package com.example.focusify.ui.onboarding

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.focusify.data.AuthRepository

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val isSuccess: Boolean = false,
    val isResetEmailSent: Boolean = false
)

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun signUp(email: String, password: String, confirmPassword: String) {
        if (!validateSignUpFields(email, password, confirmPassword)) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = authRepository.signUp(email, password)
            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            } else {
                _uiState.update { it.copy(isLoading = false, error = result.exceptionOrNull()?.message ?: "Unknown error") }
            }
        }
    }

    fun login(email: String, password: String) {
        if (!validateLoginFields(email, password)) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = authRepository.login(email, password)
            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            } else {
                _uiState.update { it.copy(isLoading = false, error = result.exceptionOrNull()?.message ?: "Unknown error") }
            }
        }
    }

    fun resetPassword(email: String) {
        if (!validateEmail(email)) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = authRepository.sendPasswordResetEmail(email)
            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, isResetEmailSent = true) }
            } else {
                _uiState.update { it.copy(isLoading = false, error = result.exceptionOrNull()?.message ?: "Unknown error") }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null, emailError = null, passwordError = null, confirmPasswordError = null) }
    }

    fun resetSuccess() {
        _uiState.update { it.copy(isSuccess = false, isResetEmailSent = false) }
    }

    private fun validateSignUpFields(email: String, password: String, confirmPassword: String): Boolean {
        var isValid = true
        if (!validateEmail(email)) isValid = false
        if (!validatePasswordComplexity(password)) isValid = false
        if (!validateConfirmPassword(password, confirmPassword)) isValid = false
        return isValid
    }

    private fun validateLoginFields(email: String, password: String): Boolean {
        var isValid = true
        if (!validateEmail(email)) isValid = false
        if (password.isBlank()) {
            _uiState.update { it.copy(passwordError = "Password cannot be empty") }
            isValid = false
        } else if (password.length < 8) {
            _uiState.update { it.copy(passwordError = "Password must be at least 8 characters") }
            isValid = false
        } else {
            _uiState.update { it.copy(passwordError = null) }
        }
        return isValid
    }

    private fun validateEmail(email: String): Boolean {
        return if (email.isBlank()) {
            _uiState.update { it.copy(emailError = "Email cannot be empty") }
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.update { it.copy(emailError = "Invalid email format") }
            false
        } else {
            _uiState.update { it.copy(emailError = null) }
            true
        }
    }

    private fun validatePasswordComplexity(password: String): Boolean {
        return when {
            password.isBlank() -> {
                _uiState.update { it.copy(passwordError = "Password cannot be empty") }
                false
            }
            password.length < 8 -> {
                _uiState.update { it.copy(passwordError = "Password must be at least 8 characters") }
                false
            }
            !password.any { it.isDigit() } -> {
                _uiState.update { it.copy(passwordError = "Password must contain at least one digit") }
                false
            }
            !password.any { it.isUpperCase() } -> {
                _uiState.update { it.copy(passwordError = "Password must contain at least one uppercase letter") }
                false
            }
            !password.any { !it.isLetterOrDigit() } -> {
                _uiState.update { it.copy(passwordError = "Password must contain at least one special character") }
                false
            }
            else -> {
                _uiState.update { it.copy(passwordError = null) }
                true
            }
        }
    }

    private fun validateConfirmPassword(password: String, confirmPassword: String): Boolean {
        return if (confirmPassword != password) {
            _uiState.update { it.copy(confirmPasswordError = "Passwords do not match") }
            false
        } else {
            _uiState.update { it.copy(confirmPasswordError = null) }
            true
        }
    }
}
