package com.example.pszzapp.domain.repository

import com.example.pszzapp.data.model.CreateFeedingModel
import com.example.pszzapp.data.model.FeedingModel
import com.example.pszzapp.presentation.feeding.CreateFeedingState

interface FeedingRepository {
    suspend fun getFeedingsByHiveId(hiveId: String): List<FeedingModel>
    suspend fun createFeeding(createFeedingModel: CreateFeedingModel): CreateFeedingState
    suspend fun removeFeeding(feedingId: String): CreateFeedingState
}