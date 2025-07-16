package com.example.FarmGame.domain.usecase.field

import com.example.FarmGame.data.model.FieldModel
import com.example.FarmGame.domain.repository.FieldsRepository
import com.example.FarmGame.presentation.dashboard.FieldState
import org.koin.core.annotation.Single

@Single
class RemoveFieldUseCase(
    private val fieldsRepository: FieldsRepository
) {
    suspend operator fun invoke(fieldModel: FieldModel): FieldState {
        return fieldsRepository.removeField(fieldModel)
    }
}