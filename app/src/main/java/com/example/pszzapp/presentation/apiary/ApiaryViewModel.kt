package com.example.pszzapp.presentation.apiary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pszzapp.data.model.HiveModel
import com.example.pszzapp.domain.usecase.apiary.GetHivesByApiaryIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class ApiaryViewModel(
    id: String,
    private val getHivesByApiaryIdUseCase: GetHivesByApiaryIdUseCase
) : ViewModel() {
    private val _apiaryState: MutableStateFlow<ApiaryState> = MutableStateFlow(ApiaryState.Loading)
    val apiaryState: StateFlow<ApiaryState> = _apiaryState

    init {
        getHivesByApiaryId(id)
    }

    private fun getHivesByApiaryId(id: String) {
        viewModelScope.launch {
            try {
                val hives = getHivesByApiaryIdUseCase(id)
                _apiaryState.value = ApiaryState.Success(hives)
            } catch (e: Exception) {
                _apiaryState.value = ApiaryState.Error("Failed: ${e.message}")
            }
        }
    }
}

sealed class ApiaryState {
    data object Loading : ApiaryState()
    data class Success(val hives: List<HiveModel>) : ApiaryState()
    data class Error(val message: String) : ApiaryState()
}