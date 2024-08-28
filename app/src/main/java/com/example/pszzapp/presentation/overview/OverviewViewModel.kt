package com.example.pszzapp.presentation.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pszzapp.data.model.HiveModel
import com.example.pszzapp.data.model.OverviewModel
import com.example.pszzapp.domain.usecase.hive.GetHiveByIdUseCase
import com.example.pszzapp.domain.usecase.overview.CreateOverviewUseCase
import com.example.pszzapp.domain.usecase.overview.GetOverviewByIdUseCase
import com.example.pszzapp.domain.usecase.overview.GetOverviewsByHiveIdUseCase
import com.example.pszzapp.presentation.apiaries.ApiariesState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
class OverviewViewModel(
    id: String,
    private val getHiveByIdUseCase: GetHiveByIdUseCase,
    private val getOverviewByIdUseCase: GetOverviewByIdUseCase
) : ViewModel() {
    private val _overviewState: MutableStateFlow<OverviewState> = MutableStateFlow(
        OverviewState.Loading
    )
    val overviewState: StateFlow<OverviewState> = _overviewState


    init {
        getOverviewById(id)
    }

    private fun getOverviewById(id: String) {
        _overviewState.value = OverviewState.Loading

        viewModelScope.launch {
            try {
                val overview = getOverviewByIdUseCase(id)

                if (overview != null) {
                    _overviewState.value = OverviewState.Success(overview)
                } else {
                    _overviewState.value = OverviewState.Error("Failed: Nie znaleziono przeglądu o podanym ID")
                }
            } catch (e: Exception) {
                _overviewState.value = OverviewState.Error("Failed: ${e.message}")
            }
        }
    }
}

sealed class OverviewState {
    data object Loading : OverviewState()
    data class Success(val overview: OverviewModel) : OverviewState()
    data class Error(val message: String) : OverviewState()
}