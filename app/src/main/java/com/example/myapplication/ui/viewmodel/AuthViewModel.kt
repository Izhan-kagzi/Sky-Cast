package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.Resource
import com.example.myapplication.data.model.User
import com.example.myapplication.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<Resource<User>?>(null)
    val authState: StateFlow<Resource<User>?> = _authState.asStateFlow()

    private val _resetPasswordState = MutableStateFlow<Resource<Unit>?>(null)
    val resetPasswordState: StateFlow<Resource<Unit>?> = _resetPasswordState.asStateFlow()

    fun isUserLoggedIn(): Boolean = authRepository.isUserLoggedIn()

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = Resource.Error("Please enter email and password")
            return
        }

        viewModelScope.launch {
            _authState.value = Resource.Loading
            _authState.value = authRepository.loginUser(email.trim(), password)
        }
    }

    fun register(name: String, email: String, password: String, confirmPassword: String) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _authState.value = Resource.Error("Please fill in all fields")
            return
        }

        if (password != confirmPassword) {
            _authState.value = Resource.Error("Passwords do not match")
            return
        }

        if (password.length < 6) {
            _authState.value = Resource.Error("Password must be at least 6 characters")
            return
        }

        viewModelScope.launch {
            _authState.value = Resource.Loading
            _authState.value = authRepository.registerUser(name.trim(), email.trim(), password)
        }
    }

    fun sendPasswordReset(email: String) {
        if (email.isBlank()) {
            _resetPasswordState.value = Resource.Error("Please enter your email address")
            return
        }

        viewModelScope.launch {
            _resetPasswordState.value = Resource.Loading
            _resetPasswordState.value = authRepository.sendPasswordResetEmail(email.trim())
        }
    }

    fun logout() {
        authRepository.logout()
        _authState.value = null
    }

    fun clearState() {
        _authState.value = null
        _resetPasswordState.value = null
    }
}
