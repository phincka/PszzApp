package com.example.pszzapp.presentation.hive

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pszzapp.data.model.HiveModel
import com.example.pszzapp.data.model.ListItemOverviewModel
import com.example.pszzapp.domain.usecase.hive.GetHiveByIdUseCase
import com.example.pszzapp.domain.usecase.overview.GetLastOverviewIdUseCase
import com.example.pszzapp.domain.usecase.overview.GetOverviewsByHiveIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
class HiveViewModel(
    id: String,
    private val getHiveByIdUseCase: GetHiveByIdUseCase,
    private val getOverviewsByHiveIdUseCase: GetOverviewsByHiveIdUseCase,
    private val getLastOverviewIdUseCase: GetLastOverviewIdUseCase,
) : ViewModel() {
    private val _hiveState: MutableStateFlow<HiveState> = MutableStateFlow(HiveState.Loading)
    val hiveState: StateFlow<HiveState> = _hiveState

    private val _overviewsState: MutableStateFlow<OverviewsState> = MutableStateFlow(OverviewsState.None)
    val overviewsState: StateFlow<OverviewsState> = _overviewsState

    private val _lastOverviewIdState: MutableStateFlow<LastOverviewIdState> = MutableStateFlow(LastOverviewIdState.None)
    val lastOverviewIdState: StateFlow<LastOverviewIdState> = _lastOverviewIdState

    init {
        getHiveById(id)
        getLastOverviewId(id)
    }

    private fun getHiveById(id: String) {
        _hiveState.value = HiveState.Loading

        viewModelScope.launch {
            try {
                val hive = getHiveByIdUseCase(id)

                if (hive != null) {
                    _hiveState.value = HiveState.Success(hive)
                } else {
                    _hiveState.value = HiveState.Error("Failed: Nie znaleziono ula o podanym ID")
                }
            } catch (e: Exception) {
                _hiveState.value = HiveState.Error("Failed: ${e.message}")
            }
        }
    }

    private fun getLastOverviewId(hiveId: String) {
        viewModelScope.launch {
            try {
                val lastOverviewId = getLastOverviewIdUseCase(hiveId)

                _lastOverviewIdState.value = LastOverviewIdState.Success(lastOverviewId)
            } catch (e: Exception) {
            _lastOverviewIdState.value = LastOverviewIdState.Error("Failed: ${e.message}")
            }
        }
    }

    fun geOverviewsByHiveId(hiveId: String) {
        Log.d("LOG_H", "ŁADOWANIE przeglądów!")
        _overviewsState.value = OverviewsState.Loading

        viewModelScope.launch {
            try {
                val overviews = getOverviewsByHiveIdUseCase(id = hiveId)
                Log.d("LOG_H", "ZAładowano przeglądów!")

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
    data class Error(val message: String) : HiveState()
}

sealed class LastOverviewIdState {
    data object None : LastOverviewIdState()
    data class Success(val overviewId: String?) : LastOverviewIdState()
    data class Error(val message: String) : LastOverviewIdState()
}

sealed class OverviewsState {
    data object None : OverviewsState()
    data object Loading : OverviewsState()
    data class Success(val overviews: List<ListItemOverviewModel>) : OverviewsState()
    data class Error(val message: String) : OverviewsState()
}
