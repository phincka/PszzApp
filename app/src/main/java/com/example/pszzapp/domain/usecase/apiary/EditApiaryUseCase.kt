package com.example.pszzapp.domain.usecase.apiary

import com.example.pszzapp.data.model.ApiaryModel
import com.example.pszzapp.domain.repository.ApiaryRepository
import org.koin.core.annotation.Single

@Single
class EditApiaryUseCase(
    private val apiaryRepository: ApiaryRepository
) {
    suspend operator fun invoke(apiaryModel: ApiaryModel) = apiaryRepository.editApiary(apiaryModel)
}