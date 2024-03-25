package com.example.pszzapp.domain.repository

import com.example.pszzapp.data.model.ApiaryModel
import com.example.pszzapp.data.model.HiveModel

interface ApiaryRepository {
    suspend fun getApiaries(): List<ApiaryModel>
    suspend fun getHivesByApiaryId(id: String): List<HiveModel>
}