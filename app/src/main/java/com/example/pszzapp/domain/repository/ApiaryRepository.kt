package com.example.pszzapp.domain.repository

import com.example.pszzapp.data.model.ApiaryModel
import com.example.pszzapp.data.model.HiveModel
import com.example.pszzapp.presentation.apiaries.create.CreateApiaryState
import com.example.pszzapp.presentation.hive.create.CreateHiveState

interface ApiaryRepository {
    suspend fun getApiaries(): List<ApiaryModel>
    suspend fun createApiary(apiaryModel: ApiaryModel): CreateApiaryState
}