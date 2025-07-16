package com.example.FarmGame.domain.usecase.field

import com.example.FarmGame.domain.repository.FieldsRepository
import com.example.FarmGame.presentation.dashboard.FieldState
import org.koin.core.annotation.Single

@Single
class ClearFieldsUseCase(
    private val fieldsRepository: FieldsRepository
) {
    suspend operator fun invoke(): FieldState {
        return fieldsRepository.clearFields()
    }
}