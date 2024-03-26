package com.example.pszzapp.domain.usecase.hive

import com.example.pszzapp.data.model.HiveModel
import com.example.pszzapp.domain.repository.HiveRepository
import org.koin.core.annotation.Single

@Single
class GetHivesByApiaryIdUseCase(
    private val hiveRepository: HiveRepository
) {
    suspend operator fun invoke(id: String): List<HiveModel> {
        return hiveRepository.getHivesByApiaryId(id)
    }
}