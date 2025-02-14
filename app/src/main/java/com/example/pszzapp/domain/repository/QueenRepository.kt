package com.example.pszzapp.domain.repository

import com.example.pszzapp.data.model.QueenModel
import com.example.pszzapp.presentation.queen.RemoveQueenState
import com.example.pszzapp.presentation.queen.create.CreateQueenState

interface QueenRepository {
    suspend fun getQueens(hiveId: String): List<QueenModel>
    suspend fun getQueenById(queenId: String): QueenModel?
    suspend fun createQueen(queenModel: QueenModel): CreateQueenState
    suspend fun editQueen(queenModel: QueenModel): CreateQueenState
    suspend fun removeQueen(queenId: String): RemoveQueenState
}