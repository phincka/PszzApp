package com.example.FarmGame.domain.repository

import com.example.FarmGame.data.model.FieldModel
import com.example.FarmGame.presentation.dashboard.FieldState

interface FieldsRepository {
    suspend fun loadFields(): List<FieldModel>
    suspend fun updateField(fieldModel: FieldModel): FieldState
    suspend fun addField(fieldModel: FieldModel): FieldState
    suspend fun removeField(fieldModel: FieldModel): FieldState
    suspend fun clearFields(): FieldState
}