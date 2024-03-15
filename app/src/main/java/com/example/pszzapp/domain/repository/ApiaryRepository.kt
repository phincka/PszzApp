package com.example.pszzapp.domain.repository

import com.example.pszzapp.data.model.ApiaryModel

interface ApiaryRepository {
    suspend fun getApiaries(): List<ApiaryModel>
}