package com.example.pszzapp.domain.repository

import com.example.pszzapp.data.util.AccountUserState
import com.example.pszzapp.data.util.AuthState


interface AuthRepository {
    suspend fun getCurrentUser(): AccountUserState
    suspend fun firebaseEmailSignIn(email: String, password: String): AuthState
    suspend fun firebaseEmailSignUp(email: String, password: String, repeatPassword: String): AuthState
    suspend fun firebaseSignOut(): AccountUserState
    suspend fun checkEmailVerification(): AuthState
    suspend fun resendVerificationEmail(): AuthState
}