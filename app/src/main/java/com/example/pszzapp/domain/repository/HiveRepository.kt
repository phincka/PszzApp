package com.example.pszzapp.domain.repository

import com.example.pszzapp.data.model.HiveInfoModel
import com.example.pszzapp.data.model.HiveModel
import com.example.pszzapp.presentation.hive.RemoveHiveState
import com.example.pszzapp.presentation.hive.create.CreateHiveState
import com.example.pszzapp.presentation.hiveInfo.HiveInfoState

interface HiveRepository {
    suspend fun getHivesByApiaryId(id: String): List<HiveModel>
    suspend fun getHiveById(id: String): HiveModel?
    suspend fun createHive(hiveModel: HiveModel): CreateHiveState
    suspend fun editHive(hiveModel: HiveModel): CreateHiveState
    suspend fun removeHive(hiveId: String): RemoveHiveState

    suspend fun getHiveInfo(id: String): HiveInfoState
}