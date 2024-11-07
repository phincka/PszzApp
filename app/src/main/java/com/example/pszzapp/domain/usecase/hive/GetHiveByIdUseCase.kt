package com.example.pszzapp.domain.usecase.hive

import com.example.pszzapp.data.model.HiveModel
import com.example.pszzapp.domain.repository.HiveRepository
import org.koin.core.annotation.Single

@Single
class GetHiveByIdUseCase(
    private val hiveRepository: HiveRepository
) {
    suspend operator fun invoke(hiveId: String): HiveModel? {
        return hiveRepository.getHiveById(hiveId)
    }
}