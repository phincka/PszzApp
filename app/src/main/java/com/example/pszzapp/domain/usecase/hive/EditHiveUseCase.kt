package com.example.pszzapp.domain.usecase.hive

import com.example.pszzapp.data.model.HiveModel
import com.example.pszzapp.domain.repository.ApiaryRepository
import com.example.pszzapp.domain.repository.HiveRepository
import org.koin.core.annotation.Single

@Single
class EditHiveUseCase(
    private val hiveRepository: HiveRepository
) {
    suspend operator fun invoke(hiveModel: HiveModel) = hiveRepository.editHive(hiveModel)
}