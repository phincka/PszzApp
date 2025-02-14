package com.example.pszzapp.presentation.queen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pszzapp.data.model.ApiaryModel
import com.example.pszzapp.data.model.HiveModel
import com.example.pszzapp.data.model.QueenModel
import com.example.pszzapp.domain.usecase.apiary.GetApiaryByIdUseCase
import com.example.pszzapp.domain.usecase.apiary.RemoveApiaryUseCase
import com.example.pszzapp.domain.usecase.hive.GetHivesByApiaryIdUseCase
import com.example.pszzapp.domain.usecase.queen.GetQueensUseCase
import com.example.pszzapp.presentation.apiary.ApiaryState
import com.example.pszzapp.presentation.apiary.RemoveApiaryState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class QueenViewModel(
    id: String,
    private val getQueenUseCase: GetQueensUseCase,
) : ViewModel() {
    private val _queenState: MutableStateFlow<QueenState> = MutableStateFlow(QueenState.Loading)
    val queenState: StateFlow<QueenState> = _queenState

    private val _removeQueenState: MutableStateFlow<RemoveQueenState> = MutableStateFlow(
        RemoveQueenState.None
    )
    val removeQueenState: StateFlow<RemoveQueenState> = _removeQueenState

    init {
        getQueens(id)
    }

    fun getQueens(id: String) {
        _queenState.value = QueenState.Loading

        viewModelScope.launch {
            try {
                val queen = getQueenUseCase(id)

//                if (queen != null) {
//                    _queenState.value = QueenState.Success(
//                        queen = queen,
//                    )
//                } else {
//                    _queenState.value = QueenState.Error("Failed: TODO")
//                }

            } catch (e: Exception) {
                _queenState.value = QueenState.Error("Failed: ${e.message}")
            }
        }
    }
}

sealed class QueenState {
    data object Loading : QueenState()
    data class Success(
        val queen: QueenModel,
    ) : QueenState()
    data class Error(val message: String) : QueenState()
}

sealed class RemoveQueenState {
    data object None : RemoveQueenState()
    data object Success : RemoveQueenState()
    data class Error(val message: String) : RemoveQueenState()
}