package com.example.pszzapp.presentation.apiaries.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pszzapp.data.model.ApiaryModel
import com.example.pszzapp.domain.usecase.apiary.CreateApiaryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
class CreateApiaryViewModel(
    private val createApiaryUseCase: CreateApiaryUseCase
) : ViewModel() {
    private val _createApiaryState: MutableStateFlow<CreateApiaryState> = MutableStateFlow(CreateApiaryState.Success)
    val createApiaryState: StateFlow<CreateApiaryState> = _createApiaryState

    fun createApiary(apiaryData: ApiaryModel) {
        viewModelScope.launch {
            _createApiaryState.value = CreateApiaryState.Loading
            _createApiaryState.value = createApiaryUseCase(apiaryData)
        }
    }
}

sealed class CreateApiaryState {
    data object Loading : CreateApiaryState()
    data object Success : CreateApiaryState()
    data class Redirect(val apiaryId: String) : CreateApiaryState()
    data class Error(val message: String) : CreateApiaryState()
}