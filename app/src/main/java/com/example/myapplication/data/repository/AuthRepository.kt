package com.example.myapplication.data.repository

import com.example.myapplication.data.model.Resource
import com.example.myapplication.data.model.User

interface AuthRepository {
    suspend fun registerUser(name: String, email: String, password: String): Resource<User>
    suspend fun loginUser(email: String, password: String): Resource<User>
    suspend fun sendPasswordResetEmail(email: String): Resource<Unit>
    fun logout()
    fun isUserLoggedIn(): Boolean
    fun getCurrentUserId(): String?
    suspend fun getUserProfile(userId: String): Resource<User>
}
