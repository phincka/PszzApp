package com.example.pszzapp.presentation.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pszzapp.data.model.DetailedOverviewModel
import com.example.pszzapp.data.model.OverviewModel
import com.example.pszzapp.domain.usecase.hive.GetHiveByIdUseCase
import com.example.pszzapp.domain.usecase.overview.GetOverviewByIdUseCase
import com.example.pszzapp.domain.usecase.overview.RemoveOverviewUseCase
import com.example.pszzapp.presentation.hive.RemoveHiveState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
class OverviewViewModel(
    id: String,
    private val getOverviewByIdUseCase: GetOverviewByIdUseCase,
    private val removeOverviewUseCase: RemoveOverviewUseCase,
) : ViewModel() {
    private val _overviewState: MutableStateFlow<OverviewState> = MutableStateFlow(
        OverviewState.Loading
    )
    val overviewState: StateFlow<OverviewState> = _overviewState

    private val _removeOverviewState: MutableStateFlow<RemoveOverviewState> = MutableStateFlow(RemoveOverviewState.None)
    val removeOverviewState: StateFlow<RemoveOverviewState> = _removeOverviewState

    init {
        getOverviewById(id)
    }

    fun getOverviewById(id: String) {
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

    fun removeOverview(
        overviewId: String,
        hiveId: String,
    ) {
        viewModelScope.launch {
            try {
                _removeOverviewState.value = removeOverviewUseCase(overviewId = overviewId, hiveId = hiveId)
            } catch (e: Exception) {
                _removeOverviewState.value = RemoveOverviewState.Error("${e.message}")
            }
        }
    }
}

sealed class OverviewState {
    data object Loading : OverviewState()
    data class Success(val overview: OverviewModel) : OverviewState()
    data class Error(val message: String) : OverviewState()
}

sealed class RemoveOverviewState {
    data object None : RemoveOverviewState()
    data class Success(val hiveId: String) : RemoveOverviewState()
    data class Error(val message: String) : RemoveOverviewState()
}