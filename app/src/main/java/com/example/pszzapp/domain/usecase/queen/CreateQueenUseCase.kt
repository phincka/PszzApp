package com.example.pszzapp.domain.usecase.queen

import com.example.pszzapp.data.model.QueenModel
import com.example.pszzapp.domain.repository.QueenRepository
import org.koin.core.annotation.Single

@Single
class CreateQueenUseCase(
    private val queenRepository: QueenRepository
) {
    suspend operator fun invoke(queenModel: QueenModel) = queenRepository.createQueen(queenModel)
}