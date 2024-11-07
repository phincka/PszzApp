package com.example.pszzapp.presentation.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pszzapp.data.model.DetailedOverviewModel
import com.example.pszzapp.domain.usecase.hive.GetHiveByIdUseCase
import com.example.pszzapp.domain.usecase.overview.GetDetailedOverviewByIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
class OverviewViewModel(
    id: String,
    private val getHiveByIdUseCase: GetHiveByIdUseCase,
    private val getDetailedOverviewByIdUseCase: GetDetailedOverviewByIdUseCase
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
                val overview = getDetailedOverviewByIdUseCase(id)

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
    data class Success(val overview: DetailedOverviewModel) : OverviewState()
    data class Error(val message: String) : OverviewState()
}