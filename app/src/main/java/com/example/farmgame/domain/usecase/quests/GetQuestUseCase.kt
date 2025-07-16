package com.example.FarmGame.domain.usecase.quests

import com.example.FarmGame.data.model.QuestModel
import com.example.FarmGame.domain.repository.QuestsStatusRepository
import org.koin.core.annotation.Single

@Single
class GetQuestUseCase(
    private val questsStatusRepository: QuestsStatusRepository
) {
    suspend operator fun invoke(): List<QuestModel> {
        return questsStatusRepository.getQuests()
    }
}