package com.example.pszzapp.presentation.hive.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pszzapp.data.model.HiveModel
import com.example.pszzapp.domain.usecase.hive.CreateHiveUseCase
import com.example.pszzapp.domain.usecase.overview.CreateOverviewUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
class CreateHiveViewModel(
    private val createHiveUseCase: CreateHiveUseCase
) : ViewModel() {
    private val _createHiveState: MutableStateFlow<CreateHiveState> = MutableStateFlow(CreateHiveState.Success)
    val createHiveState: StateFlow<CreateHiveState> = _createHiveState

    fun createHive(hiveModel: HiveModel) {
        viewModelScope.launch {
            _createHiveState.value = CreateHiveState.Loading
            _createHiveState.value = createHiveUseCase(hiveModel)
        }
    }
}

sealed class CreateHiveState {
    data object Loading : CreateHiveState()
    data object Success : CreateHiveState()
    data class Redirect(val hiveId: String) : CreateHiveState()
    data class Error(val message: String) : CreateHiveState()
}