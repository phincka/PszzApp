package com.example.pszzapp.domain.usecase.overview

import com.example.pszzapp.domain.repository.OverviewRepository
import org.koin.core.annotation.Single

@Single
class RemoveOverviewUseCase(
    private val overviewRepository: OverviewRepository,
) {
    suspend operator fun invoke(overviewId: String, hiveId: String) = overviewRepository.removeOverview(overviewId = overviewId, hiveId = hiveId)
}