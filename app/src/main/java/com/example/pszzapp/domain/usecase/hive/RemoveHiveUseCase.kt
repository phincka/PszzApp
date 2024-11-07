package com.example.pszzapp.domain.usecase.hive

import com.example.pszzapp.domain.repository.HiveRepository
import org.koin.core.annotation.Single

@Single
class RemoveHiveUseCase(
    private val hiveRepository: HiveRepository,
) {
    suspend operator fun invoke(hiveId: String) = hiveRepository.removeHive(hiveId = hiveId)
}