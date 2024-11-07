package com.example.pszzapp.data.model

data class UserModel (
    val uid: String,
    val name: String,
    val email: String,
    val isModalAlternativeEnable: Boolean,
    val isPremium: Boolean = false,
    val isBetaTester: Boolean = false,
    val role: UserRole = UserRole.USER,
)

enum class UserRole {
    USER,
    ADMIN,
}