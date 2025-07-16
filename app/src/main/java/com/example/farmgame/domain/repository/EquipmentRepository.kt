package com.example.FarmGame.domain.repository

import com.example.FarmGame.data.model.EquipmentModel
import com.example.FarmGame.data.model.GameItemModel
import com.example.FarmGame.presentation.dashboard.EquipmentState

interface EquipmentRepository {
    suspend fun getGameItems(): List<GameItemModel>
    suspend fun loadEquipment(): List<EquipmentModel>
    suspend fun addEquipmentItem(equipmentModel: EquipmentModel): EquipmentState
    suspend fun removeEquipmentItem(equipmentModel: EquipmentModel): EquipmentState
    suspend fun clearEquipment(): EquipmentState
}