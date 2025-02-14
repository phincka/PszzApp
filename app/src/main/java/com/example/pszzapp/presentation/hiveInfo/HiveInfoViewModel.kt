package com.example.pszzapp.presentation.hiveInfo

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pszzapp.data.model.HiveInfoModel
import com.example.pszzapp.data.model.OverviewModel
import com.example.pszzapp.domain.usecase.hive.GetHiveInfoUseCase
import com.example.pszzapp.domain.usecase.overview.GetOverviewByIdUseCase
import com.example.pszzapp.domain.usecase.overview.RemoveOverviewUseCase
import com.example.pszzapp.presentation.overview.OverviewState
import com.example.pszzapp.presentation.overview.RemoveOverviewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
class HiveInfoViewModel(
    id: String,
    private val getHiveInfoUseCase: GetHiveInfoUseCase,
) : ViewModel() {
    private val _overviewState: MutableStateFlow<HiveInfoState> = MutableStateFlow(
        HiveInfoState.Loading
    )
    val overviewState: StateFlow<HiveInfoState> = _overviewState

    private val _removeOverviewState: MutableStateFlow<RemoveOverview2State> = MutableStateFlow(
        RemoveOverview2State.None
    )
    val removeOverviewState: StateFlow<RemoveOverview2State> = _removeOverviewState

    init {
        getHiveInfo(id)
    }

    fun getHiveInfo(id: String) {
        _overviewState.value = HiveInfoState.Loading

        viewModelScope.launch {
            try {
                val hiveInfo = getHiveInfoUseCase(id)

                if (hiveInfo != null) {
                    _overviewState.value = hiveInfo
                } else {
                    _overviewState.value = HiveInfoState.Error("Failed: Nie znaleziono przeglądu o podanym ID")
                }
            } catch (e: Exception) {
                _overviewState.value = HiveInfoState.Error("Failed: ${e.message}")
            }
        }
    }

//    fun removeOverview(
//        overviewId: String,
//        hiveId: String,
//    ) {
//        viewModelScope.launch {
//            try {
//                _removeOverviewState.value = removeOverviewUseCase(overviewId = overviewId, hiveId = hiveId)
//            } catch (e: Exception) {
//                _removeOverviewState.value = RemoveOverview2State.Error("${e.message}")
//            }
//        }
//    }
}

sealed class HiveInfoState {
    data object Loading : HiveInfoState()
    data class Success(val hiveInfo: HiveInfoModel) : HiveInfoState()
    data class Error(val message: String) : HiveInfoState()
}

sealed class RemoveOverview2State {
    data object None : RemoveOverview2State()
    data class Success(val hiveId: String) : RemoveOverview2State()
    data class Error(val message: String) : RemoveOverview2State()
}