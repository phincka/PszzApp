package com.example.FarmGame.domain.usecase.auth

import com.example.FarmGame.domain.repository.AuthRepository
import org.koin.core.annotation.Single

@Single
class SignOutUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() = authRepository.firebaseSignOut()
}