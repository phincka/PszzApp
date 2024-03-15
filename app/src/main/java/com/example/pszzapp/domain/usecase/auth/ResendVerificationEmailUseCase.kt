package com.example.pszzapp.domain.usecase.auth

import com.example.pszzapp.domain.repository.AuthRepository
import org.koin.core.annotation.Single

@Single
class ResendVerificationEmailUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() = authRepository.resendVerificationEmail()
}