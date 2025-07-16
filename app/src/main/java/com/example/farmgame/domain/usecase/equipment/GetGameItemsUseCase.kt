package com.example.FarmGame.domain.usecase.apiary

import com.example.FarmGame.data.model.GameItemModel
import com.example.FarmGame.domain.repository.EquipmentRepository
import org.koin.core.annotation.Single

@Single
class GetGameItemsUseCase(
    private val equipmentRepository: EquipmentRepository
) {
    suspend operator fun invoke(): List<GameItemModel> {
        return equipmentRepository.getGameItems()
    }
}