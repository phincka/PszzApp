package com.example.pszzapp.domain.usecase.auth

import com.example.pszzapp.domain.repository.AuthRepository
import org.koin.core.annotation.Single

@Single
class SignUpUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String, repeatPassword: String) = authRepository.firebaseEmailSignUp(email, password, repeatPassword)
}