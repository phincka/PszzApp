package com.example.pszzapp.domain.usecase.feeding

import com.example.pszzapp.data.model.CreateFeedingModel
import com.example.pszzapp.data.model.HiveModel
import com.example.pszzapp.domain.repository.FeedingRepository
import com.example.pszzapp.domain.repository.HiveRepository
import org.koin.core.annotation.Single

@Single
class CreateFeedingUseCase(
    private val feedingRepository: FeedingRepository,
) {
    suspend operator fun invoke(feedingModel: CreateFeedingModel) = feedingRepository.createFeeding(feedingModel)
}