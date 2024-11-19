package com.example.pszzapp.presentation.overview.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pszzapp.data.model.HiveModel
import com.example.pszzapp.data.model.OverviewModel
import com.example.pszzapp.domain.usecase.overview.CreateOverviewUseCase
import com.example.pszzapp.domain.usecase.overview.EditOverviewUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
class CreateOverviewViewModel(
    private val createOverviewUseCase: CreateOverviewUseCase,
    private val editOverviewUseCase: EditOverviewUseCase,
) : ViewModel() {
    private val _createOverviewState: MutableStateFlow<CreateOverviewState> = MutableStateFlow(
        CreateOverviewState.Success
    )
    val createOverviewState: StateFlow<CreateOverviewState> = _createOverviewState

    fun createOverview(overviewModel: OverviewModel) {
        viewModelScope.launch {
            _createOverviewState.value = CreateOverviewState.Loading
            _createOverviewState.value = createOverviewUseCase(overviewModel)
        }
    }

    fun editOverview(overviewModel: OverviewModel) {
        viewModelScope.launch {
            _createOverviewState.value = CreateOverviewState.Loading
            _createOverviewState.value = editOverviewUseCase(overviewModel)
        }
    }
}

sealed class CreateOverviewState {
    data object Loading : CreateOverviewState()
    data object Success : CreateOverviewState()
    data class Redirect(val overviewId: String) : CreateOverviewState()
    data class Error(val message: String) : CreateOverviewState()
}