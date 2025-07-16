package com.example.FarmGame.domain.repository

import com.example.FarmGame.data.model.QuestModel
import com.example.FarmGame.data.model.QuestStatus
import com.example.FarmGame.presentation.qrScanner.QuestStatusState

interface QuestsStatusRepository {
    suspend fun getQuests(): List<QuestModel>
    suspend fun getStatuses(): List<QuestStatus>
    suspend fun updateStatus(status: QuestStatus): QuestStatusState
    suspend fun clearStatuses(): QuestStatusState
}