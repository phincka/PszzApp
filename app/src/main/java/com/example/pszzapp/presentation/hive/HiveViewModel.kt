package com.example.pszzapp.presentation.hive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pszzapp.data.model.HiveModel
import com.example.pszzapp.data.model.ListItemOverviewModel
import com.example.pszzapp.domain.usecase.hive.GetHiveByIdUseCase
import com.example.pszzapp.domain.usecase.hive.RemoveHiveUseCase
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
    private val removeHiveUseCase: RemoveHiveUseCase,
) : ViewModel() {
    private val _hiveState: MutableStateFlow<HiveState> = MutableStateFlow(HiveState.Loading)
    val hiveState: StateFlow<HiveState> = _hiveState

    private val _overviewsState: MutableStateFlow<OverviewsState> = MutableStateFlow(OverviewsState.None)
    val overviewsState: StateFlow<OverviewsState> = _overviewsState

    private val _lastOverviewIdState: MutableStateFlow<LastOverviewIdState> = MutableStateFlow(LastOverviewIdState.None)
    val lastOverviewIdState: StateFlow<LastOverviewIdState> = _lastOverviewIdState

    private val _removeHiveState: MutableStateFlow<RemoveHiveState> = MutableStateFlow(RemoveHiveState.None)
    val removeHiveState: StateFlow<RemoveHiveState> = _removeHiveState

    init {
        getHiveById(id)
        getLastOverviewId(id)
    }

    fun refreshHive(id: String) {
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
        _overviewsState.value = OverviewsState.Loading

        viewModelScope.launch {
            try {
                val overviews = getOverviewsByHiveIdUseCase(id = hiveId)
                _overviewsState.value = OverviewsState.Success(overviews)
            } catch (e: Exception) {
                _overviewsState.value = OverviewsState.Error("Failed: ${e.message}")
            }
        }
    }

    fun removeHive(
        hiveId: String,
    ) {
        viewModelScope.launch {
            try {
                _removeHiveState.value = removeHiveUseCase(hiveId = hiveId)
            } catch (e: Exception) {
                _removeHiveState.value = RemoveHiveState.Error("${e.message}")
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

sealed class RemoveHiveState {
    data object None : RemoveHiveState()
    data object Success : RemoveHiveState()
    data class Error(val message: String) : RemoveHiveState()
}