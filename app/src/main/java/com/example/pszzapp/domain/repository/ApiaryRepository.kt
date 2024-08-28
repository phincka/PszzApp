package com.example.pszzapp.domain.repository

import com.example.pszzapp.data.model.ApiaryModel
import com.example.pszzapp.data.model.HiveModel
import com.example.pszzapp.presentation.apiary.create.CreateApiaryState
import com.example.pszzapp.presentation.hive.create.CreateHiveState

interface ApiaryRepository {
    suspend fun getApiaries(): List<ApiaryModel>
    suspend fun getApiaryById(apiaryId: String): ApiaryModel?
    suspend fun createApiary(apiaryModel: ApiaryModel): CreateApiaryState
}