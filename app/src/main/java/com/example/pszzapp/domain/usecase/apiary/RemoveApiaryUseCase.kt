package com.example.pszzapp.domain.usecase.apiary

import com.example.pszzapp.domain.repository.ApiaryRepository
import org.koin.core.annotation.Single

@Single
class RemoveApiaryUseCase(
    private val apiaryRepository: ApiaryRepository
) {
    suspend operator fun invoke(apiaryId: String) = apiaryRepository.removeApiary(apiaryId)
}