package com.example.pszzapp.domain.repository

import com.example.pszzapp.data.model.ApiaryModel
import com.example.pszzapp.data.model.DetailedOverviewModel
import com.example.pszzapp.data.model.HiveModel
import com.example.pszzapp.data.model.ListItemOverviewModel
import com.example.pszzapp.data.model.OverviewModel
import com.example.pszzapp.presentation.apiary.create.CreateApiaryState
import com.example.pszzapp.presentation.hive.create.CreateHiveState
import com.example.pszzapp.presentation.overview.create.CreateOverviewState

interface OverviewRepository {
    suspend fun getOverviewsByHiveId(hiveId: String): List<ListItemOverviewModel>
    suspend fun getLastOverviews(size: Long): List<ListItemOverviewModel>
    suspend fun getOverviewById(overviewId: String): OverviewModel?
    suspend fun getLastOverviewId(hiveId: String): String?
    suspend fun getDetailedOverviewById(overviewId: String): DetailedOverviewModel?
    suspend fun createOverview(overviewModel: OverviewModel): CreateOverviewState
}