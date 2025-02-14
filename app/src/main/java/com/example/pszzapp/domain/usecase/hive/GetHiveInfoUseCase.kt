package com.example.pszzapp.domain.usecase.hive

import com.example.pszzapp.data.model.HiveInfoModel
import com.example.pszzapp.data.model.HiveModel
import com.example.pszzapp.domain.repository.HiveRepository
import com.example.pszzapp.presentation.hiveInfo.HiveInfoState
import org.koin.core.annotation.Single

@Single
class GetHiveInfoUseCase(
    private val hiveRepository: HiveRepository
) {
    suspend operator fun invoke(hiveId: String): HiveInfoState {
        return hiveRepository.getHiveInfo(hiveId)
    }
}