package com.example.pszzapp.domain.usecase.overview

import com.example.pszzapp.data.model.OverviewModel
import com.example.pszzapp.domain.repository.OverviewRepository
import org.koin.core.annotation.Single

@Single
class EditOverviewUseCase(
    private val overviewRepository: OverviewRepository
) {
    suspend operator fun invoke(overviewModel: OverviewModel) = overviewRepository.editOverview(overviewModel)
}