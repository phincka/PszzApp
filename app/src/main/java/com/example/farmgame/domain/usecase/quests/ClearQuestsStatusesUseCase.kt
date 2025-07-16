package com.example.FarmGame.domain.usecase.quests

import com.example.FarmGame.domain.repository.QuestsStatusRepository
import com.example.FarmGame.presentation.qrScanner.QuestStatusState
import org.koin.core.annotation.Single

@Single
class ClearQuestsStatusesUseCase(
    private val questsStatusRepository: QuestsStatusRepository
) {
    suspend operator fun invoke(): QuestStatusState {
        return questsStatusRepository.clearStatuses()
    }
}