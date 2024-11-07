package com.example.pszzapp.presentation.apiary.create

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pszzapp.data.model.ApiaryModel
import com.example.pszzapp.data.util.AccountUserState
import com.example.pszzapp.domain.usecase.apiary.CreateApiaryUseCase
import com.example.pszzapp.domain.usecase.apiary.EditApiaryUseCase
import com.example.pszzapp.domain.usecase.auth.GetCurrentUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
class CreateApiaryViewModel(
    private val createApiaryUseCase: CreateApiaryUseCase,
    private var editApiaryUseCase: EditApiaryUseCase,
) : ViewModel() {
    private val _createApiaryState: MutableStateFlow<CreateApiaryState> = MutableStateFlow(CreateApiaryState.None)
    val createApiaryState: StateFlow<CreateApiaryState> = _createApiaryState

    fun createApiary(apiaryData: ApiaryModel) {
        viewModelScope.launch {
            _createApiaryState.value = CreateApiaryState.Loading
            _createApiaryState.value = createApiaryUseCase(apiaryData)
        }
    }

    fun editApiary(apiaryData: ApiaryModel) {
        viewModelScope.launch {
            _createApiaryState.value = CreateApiaryState.Loading
            _createApiaryState.value = editApiaryUseCase(apiaryData)
        }
    }
}

sealed class CreateApiaryState {
    data object None : CreateApiaryState()
    data object Loading : CreateApiaryState()
    data class Redirect(val apiaryId: String) : CreateApiaryState()
    data class Error(val message: String) : CreateApiaryState()
}