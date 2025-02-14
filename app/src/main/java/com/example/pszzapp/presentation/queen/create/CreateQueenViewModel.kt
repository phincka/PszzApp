package com.example.pszzapp.presentation.queen.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pszzapp.data.model.QueenModel
import com.example.pszzapp.domain.usecase.queen.CreateQueenUseCase
import com.example.pszzapp.domain.usecase.queen.EditQueenUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class CreateQueenViewModel(
    private val createQueenUseCase: CreateQueenUseCase,
    private var editQueenUseCase: EditQueenUseCase,
) : ViewModel() {
    private val _createQueenState: MutableStateFlow<CreateQueenState> = MutableStateFlow(
        CreateQueenState.None
    )
    val createQueenState: StateFlow<CreateQueenState> = _createQueenState

    fun createQueen(queenModel: QueenModel) {
//        viewModelScope.launch {
//            _createQueenState.value = CreateQueenState.Loading
//            _createQueenState.value = createQueenUseCase(queenModel)
//        }
    }

    fun editQueen(queenModel: QueenModel) {
//        viewModelScope.launch {
//            _createQueenState.value = CreateQueenState.Loading
//            _createQueenState.value = editQueenUseCase(queenModel)
//        }
    }
}

sealed class CreateQueenState {
    data object None : CreateQueenState()
    data object Loading : CreateQueenState()
    data object Redirect : CreateQueenState()
    data class Error(val message: String) : CreateQueenState()
}