package com.example.pszzapp.presentation.hive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pszzapp.data.model.HiveModel
import com.example.pszzapp.data.model.OverviewModel
import com.example.pszzapp.domain.usecase.hive.GetHiveByIdUseCase
import com.example.pszzapp.domain.usecase.overview.CreateOverviewUseCase
import com.example.pszzapp.domain.usecase.overview.GetOverviewsByHiveIdUseCase
import com.example.pszzapp.presentation.apiaries.ApiariesState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
class HiveViewModel(
    id: String,
    private val getHiveByIdUseCase: GetHiveByIdUseCase,
    private val getOverviewsByHiveIdUseCase: GetOverviewsByHiveIdUseCase
) : ViewModel() {
    private val _hiveState: MutableStateFlow<HiveState> = MutableStateFlow(
        HiveState.Loading
    )
    val hiveState: StateFlow<HiveState> = _hiveState

    private val _overviewsState: MutableStateFlow<OverviewsState> = MutableStateFlow(
        OverviewsState.Loading
    )
    val overviewsState: StateFlow<OverviewsState> = _overviewsState

    init {
        getHiveById(id)
    }

    private fun getHiveById(id: String) {
        _hiveState.value = HiveState.Loading

        viewModelScope.launch {
            try {
                val hive = getHiveByIdUseCase(id)

                if (hive != null) {
                    _hiveState.value = HiveState.Success(hive)

                    geOverviewsByHiveId(id)
                } else {
                    _hiveState.value = HiveState.Error("Failed: Nie znaleziono ula o podanym ID")
                }
            } catch (e: Exception) {
                _hiveState.value = HiveState.Error("Failed: ${e.message}")
            }
        }
    }

    private fun geOverviewsByHiveId(id: String) {
        _overviewsState.value = OverviewsState.Loading

        viewModelScope.launch {
            try {
                val overviews = getOverviewsByHiveIdUseCase(id)

                _overviewsState.value = OverviewsState.Success(overviews)
            } catch (e: Exception) {
                _overviewsState.value = OverviewsState.Error("Failed: ${e.message}")
            }
        }
    }
}

sealed class HiveState {
    data object Loading : HiveState()
    data class Success(val hive: HiveModel) : HiveState()
//    data class Redirect(val overviewId: String) : HiveState()
    data class Error(val message: String) : HiveState()
}

sealed class OverviewsState {
    data object Loading : OverviewsState()
    data class Success(val overviews: List<OverviewModel>) : OverviewsState()
    data class Error(val message: String) : OverviewsState()
}