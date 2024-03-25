package com.example.pszzapp.presentation.apiaries.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pszzapp.data.model.ApiaryModel
import com.example.pszzapp.domain.usecase.apiary.GetApiariesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
class CreateApiaryViewModel(
    private val getApiariesUseCase: GetApiariesUseCase
) : ViewModel() {
    private val _createApiaryState: MutableStateFlow<CreateApiaryState> = MutableStateFlow(CreateApiaryState.Success)
    val createApiaryState: StateFlow<CreateApiaryState> = _createApiaryState


    fun createApiary() {
        _createApiaryState.value = CreateApiaryState.Loading

//        viewModelScope.launch {
//            try {
//                val apiaries = getApiariesUseCase()
//                _apiariesState.value = CreateApiaryState.Success(apiaries)
//            } catch (e: Exception) {
//                _apiariesState.value = CreateApiaryState.Error("Failed: ${e.message}")
//            }
//        }
    }
    private fun getApiaries() {
        _createApiaryState.value = CreateApiaryState.Loading

        viewModelScope.launch {
            try {
                val apiaries = getApiariesUseCase()
                _createApiaryState.value = CreateApiaryState.Success
            } catch (e: Exception) {
                _createApiaryState.value = CreateApiaryState.Error("Failed: ${e.message}")
            }
        }
    }
}

sealed class CreateApiaryState {
    data object Loading : CreateApiaryState()
    data object Success : CreateApiaryState()
    data class Error(val message: String) : CreateApiaryState()
}