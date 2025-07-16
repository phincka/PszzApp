package com.example.FarmGame.domain.usecase.quests

import com.example.FarmGame.data.model.QuestStatus
import com.example.FarmGame.domain.repository.QuestsStatusRepository
import org.koin.core.annotation.Single

@Single
class GetQuestsStatusesUseCase(
    private val questsStatusRepository: QuestsStatusRepository
) {
    suspend operator fun invoke(): List<QuestStatus> {
        return questsStatusRepository.getStatuses()
    }
}