package com.example.pszzapp.domain.repository

import com.example.pszzapp.data.model.ApiaryModel
import com.example.pszzapp.data.model.HiveModel
import com.example.pszzapp.data.model.OverviewModel
import com.example.pszzapp.presentation.apiary.create.CreateApiaryState
import com.example.pszzapp.presentation.hive.create.CreateHiveState
import com.example.pszzapp.presentation.overview.create.CreateOverviewState

interface OverviewRepository {
    suspend fun getOverviewsByHiveId(hiveId: String): List<OverviewModel>
    suspend fun getOverviewById(overviewId: String): OverviewModel?
    suspend fun createOverview(overviewModel: OverviewModel): CreateOverviewState
}