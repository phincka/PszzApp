package com.example.pszzapp.presentation.apiaries

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pszzapp.data.model.ApiaryModel
import com.example.pszzapp.domain.usecase.apiary.GetApiariesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class ApiariesViewModel(
    private val getApiariesUseCase: GetApiariesUseCase
) : ViewModel() {
    private val _apiariesState: MutableStateFlow<ApiariesState> = MutableStateFlow(ApiariesState.Loading)
    val apiariesState: StateFlow<ApiariesState> = _apiariesState

    init {
        getApiaries()
    }

    private fun getApiaries() {
        _apiariesState.value = ApiariesState.Loading

        viewModelScope.launch {
            try {
                val apiaries = getApiariesUseCase()
                _apiariesState.value = ApiariesState.Success(apiaries)
            } catch (e: Exception) {
                Log.d("LOG_H", e.message.toString())

                _apiariesState.value = ApiariesState.Error("Failed: ${e.message}")
            }
        }
    }
}

sealed class ApiariesState {
    data object Loading : ApiariesState()
    data class Success(val apiaries: List<ApiaryModel>) : ApiariesState()
    data class Error(val message: String) : ApiariesState()
}