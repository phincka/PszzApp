package com.example.pszzapp.presentation.apiary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pszzapp.data.model.UserModel
import com.example.pszzapp.data.util.AuthState
import com.example.pszzapp.domain.usecase.apiary.GetApiariesUseCase
import com.example.pszzapp.domain.usecase.auth.GetCurrentUserUseCase
import com.example.pszzapp.domain.usecase.auth.SignOutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class ApiariesViewModel(
    private val getApiariesUseCase: GetApiariesUseCase
) : ViewModel() {
    private val _apiariesState: MutableStateFlow<ApiariesState> = MutableStateFlow(ApiariesState.Loading)
    val apiariesState: StateFlow<ApiariesState> = _apiariesState

    init {
        getAllHives()
    }

    private fun getAllHives() {
        viewModelScope.launch {
            try {
                val apiaries = getApiariesUseCase()
                _apiariesState.value = ApiariesState.Success(apiaries)
            } catch (e: Exception) {
                _apiariesState.value = ApiariesState.Error("Failed: ${e.message}")
            }
        }
    }
}