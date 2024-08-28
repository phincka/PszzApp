package com.example.pszzapp.domain.usecase.overview

import com.example.pszzapp.data.model.OverviewModel
import com.example.pszzapp.domain.repository.OverviewRepository
import org.koin.core.annotation.Single

@Single
class CreateOverviewUseCase(
    private val overviewRepository: OverviewRepository
) {
    suspend operator fun invoke(overviewModel: OverviewModel) = overviewRepository.createOverview(overviewModel)
}