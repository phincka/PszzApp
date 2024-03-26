package com.example.pszzapp.domain.repository

import com.example.pszzapp.data.model.ApiaryModel
import com.example.pszzapp.data.model.HiveModel
import com.example.pszzapp.presentation.apiaries.create.CreateApiaryState
import com.example.pszzapp.presentation.hive.create.CreateHiveState

interface HiveRepository {
    suspend fun getHivesByApiaryId(id: String): List<HiveModel>
    suspend fun createHive(hiveModel: HiveModel): CreateHiveState
}