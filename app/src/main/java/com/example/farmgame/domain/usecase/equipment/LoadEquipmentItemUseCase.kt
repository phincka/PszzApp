package com.example.FarmGame.domain.usecase.apiary

import com.example.FarmGame.data.model.EquipmentModel
import com.example.FarmGame.domain.repository.EquipmentRepository
import com.example.FarmGame.presentation.dashboard.EquipmentState
import org.koin.core.annotation.Single

@Single
class LoadEquipmentUseCase(
    private val equipmentRepository: EquipmentRepository
) {
    suspend operator fun invoke(): List<EquipmentModel> {
        return equipmentRepository.loadEquipment()
    }
}