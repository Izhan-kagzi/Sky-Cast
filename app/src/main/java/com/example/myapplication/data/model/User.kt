package com.example.myapplication.data.model

data class User(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
