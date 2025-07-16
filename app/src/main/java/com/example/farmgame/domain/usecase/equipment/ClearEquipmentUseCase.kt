package com.example.FarmGame.domain.usecase.apiary

import com.example.FarmGame.domain.repository.EquipmentRepository
import com.example.FarmGame.presentation.dashboard.EquipmentState
import org.koin.core.annotation.Single

@Single
class ClearEquipmentUseCase(
    private val equipmentRepository: EquipmentRepository
) {
    suspend operator fun invoke(): EquipmentState {
        return equipmentRepository.clearEquipment()
    }
}