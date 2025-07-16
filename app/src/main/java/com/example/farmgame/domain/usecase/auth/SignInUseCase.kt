package com.example.FarmGame.domain.usecase.auth

import com.example.FarmGame.domain.repository.AuthRepository
import org.koin.core.annotation.Single

@Single
class SignInUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String) = authRepository.firebaseEmailSignIn(email, password)
}