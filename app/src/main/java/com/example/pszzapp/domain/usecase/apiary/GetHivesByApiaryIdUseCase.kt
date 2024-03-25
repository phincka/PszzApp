package com.example.pszzapp.domain.usecase.apiary

import com.example.pszzapp.data.model.ApiaryModel
import com.example.pszzapp.data.model.HiveModel
import com.example.pszzapp.domain.repository.ApiaryRepository
import org.koin.core.annotation.Single

@Single
class GetHivesByApiaryIdUseCase(
    private val apiaryRepository: ApiaryRepository
) {
    suspend operator fun invoke(id: String): List<HiveModel> {
        return apiaryRepository.getHivesByApiaryId(id)
    }
}