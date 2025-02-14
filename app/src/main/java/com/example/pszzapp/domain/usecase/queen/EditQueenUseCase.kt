package com.example.pszzapp.domain.usecase.queen

import com.example.pszzapp.data.model.ApiaryModel
import com.example.pszzapp.data.model.QueenModel
import com.example.pszzapp.domain.repository.ApiaryRepository
import com.example.pszzapp.domain.repository.QueenRepository
import org.koin.core.annotation.Single

@Single
class EditQueenUseCase(
    private val queenRepository: QueenRepository
) {
    suspend operator fun invoke(queenModel: QueenModel) = queenRepository.editQueen(queenModel)
}