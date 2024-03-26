package com.example.pszzapp.domain.usecase.apiary

import com.example.pszzapp.data.model.ApiaryModel
import com.example.pszzapp.domain.repository.ApiaryRepository
import com.example.pszzapp.presentation.apiaries.create.CreateApiaryState
import org.koin.core.annotation.Single

@Single
class CreateApiaryUseCase(
    private val apiaryRepository: ApiaryRepository
) {
    suspend operator fun invoke(apiaryModel: ApiaryModel) = apiaryRepository.createApiary(apiaryModel)
}