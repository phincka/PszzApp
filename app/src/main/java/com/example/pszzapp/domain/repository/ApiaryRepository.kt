package com.example.pszzapp.domain.repository

import com.example.pszzapp.data.model.ApiaryModel
import com.example.pszzapp.presentation.apiary.RemoveApiaryState
import com.example.pszzapp.presentation.apiary.create.CreateApiaryState

interface ApiaryRepository {
    suspend fun getApiaries(): List<ApiaryModel>
    suspend fun getApiaryById(apiaryId: String): ApiaryModel?
    suspend fun createApiary(apiaryModel: ApiaryModel): CreateApiaryState
    suspend fun removeApiary(apiaryId: String): RemoveApiaryState
}