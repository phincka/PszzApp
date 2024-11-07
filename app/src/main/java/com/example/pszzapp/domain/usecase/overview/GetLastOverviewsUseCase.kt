package com.example.pszzapp.domain.usecase.overview

import com.example.pszzapp.data.model.ListItemOverviewModel
import com.example.pszzapp.data.model.OverviewModel
import com.example.pszzapp.domain.repository.OverviewRepository
import org.koin.core.annotation.Single

@Single
class GetLastOverviewsUseCase(
    private val overviewRepository: OverviewRepository
) {
    suspend operator fun invoke(size: Long): List<ListItemOverviewModel> {
        return overviewRepository.getLastOverviews(size)
    }
}