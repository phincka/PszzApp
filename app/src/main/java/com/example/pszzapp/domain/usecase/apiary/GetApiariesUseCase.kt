package com.example.pszzapp.domain.usecase.apiary

import com.example.pszzapp.data.model.ApiaryModel
import com.example.pszzapp.domain.repository.ApiaryRepository
import org.koin.core.annotation.Single

@Single
class GetApiariesUseCase(
    private val apiaryRepository: ApiaryRepository
) {
    suspend operator fun invoke(): List<ApiaryModel> {
        return apiaryRepository.getApiaries()
    }
}