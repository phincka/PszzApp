package com.example.pszzapp.domain.usecase.apiary

import com.example.pszzapp.data.model.ApiaryModel
import com.example.pszzapp.domain.repository.ApiaryRepository
import org.koin.core.annotation.Single

@Single
class GetApiaryByIdUseCase(
    private val apiaryRepository: ApiaryRepository
) {
    suspend operator fun invoke(apiaryId: String): ApiaryModel? {
        return apiaryRepository.getApiaryById(apiaryId = apiaryId)
    }
}