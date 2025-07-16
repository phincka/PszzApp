package com.example.FarmGame.domain.usecase.field

import com.example.FarmGame.data.model.FieldModel
import com.example.FarmGame.domain.repository.FieldsRepository
import org.koin.core.annotation.Single

@Single
class LoadFieldsUseCase(
    private val fieldsRepository: FieldsRepository
) {
    suspend operator fun invoke(): List<FieldModel> {
        return fieldsRepository.loadFields()
    }
}