package com.example.pszzapp.domain.repository

import com.example.pszzapp.data.model.ListItemOverviewModel
import com.example.pszzapp.data.model.OverviewModel
import com.example.pszzapp.presentation.overview.RemoveOverviewState
import com.example.pszzapp.presentation.overview.create.CreateOverviewState

interface OverviewRepository {
    suspend fun getOverviewsByHiveId(hiveId: String): List<ListItemOverviewModel>
    suspend fun getLastOverviews(size: Long): List<ListItemOverviewModel>
    suspend fun getOverviewById(overviewId: String): OverviewModel?
    suspend fun getLastOverviewId(hiveId: String): String?
    suspend fun createOverview(overviewModel: OverviewModel): CreateOverviewState
    suspend fun editOverview(overviewModel: OverviewModel): CreateOverviewState
    suspend fun removeOverview(overviewId: String, hiveId: String): RemoveOverviewState
}