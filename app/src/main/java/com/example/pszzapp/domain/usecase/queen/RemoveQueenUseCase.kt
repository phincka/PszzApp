package com.example.pszzapp.domain.usecase.queen

import com.example.pszzapp.domain.repository.QueenRepository
import org.koin.core.annotation.Single

@Single
class RemoveQueenUseCase(
    private val queenRepository: QueenRepository
) {
    suspend operator fun invoke(queenId: String) = queenRepository.removeQueen(queenId)
}