package com.example.pszzapp.presentation.feeding

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
class FeedingScreenViewModel(
    id: String,
    private val getHiveByIdUseCase: GetHiveByIdUseCase,
    private val getDetailedOverviewByIdUseCase: GetDetailedOverviewByIdUseCase
) : ViewModel() {
    private val _overviewState: MutableStateFlow<FeedingScreenState> = MutableStateFlow(
        FeedingScreenState.Loading
    )
    val overviewState: StateFlow<FeedingScreenState> = _overviewState


    init {
        getOverviewById(id)
    }

    private fun getOverviewById(id: String) {
        _overviewState.value = FeedingScreenState.Loading

        viewModelScope.launch {
            try {
                val overview = getDetailedOverviewByIdUseCase(id)

                if (overview != null) {
                    _overviewState.value = FeedingScreenState.Success(overview)
                } else {
                    _overviewState.value = FeedingScreenState.Error("Failed: Nie znaleziono przeglądu o podanym ID")
                }
            } catch (e: Exception) {
                _overviewState.value = FeedingScreenState.Error("Failed: ${e.message}")
            }
        }
    }
}

sealed class FeedingScreenState {
    data object Loading : FeedingScreenState()
    data class Success(val overview: DetailedOverviewModel) : FeedingScreenState()
    data class Error(val message: String) : FeedingScreenState()
}


sealed class CreateFeedingState {
    data object Loading : CreateFeedingState()
    data object Success : CreateFeedingState()
    data class Redirect(val hiveId: String) : CreateFeedingState()
    data class Error(val message: String) : CreateFeedingState()
}

