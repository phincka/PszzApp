package com.example.pszzapp.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pszzapp.data.model.ListItemOverviewModel
import com.example.pszzapp.data.util.AccountUserState
import com.example.pszzapp.domain.usecase.auth.GetCurrentUserUseCase
import com.example.pszzapp.domain.usecase.auth.SignOutUseCase
import com.example.pszzapp.domain.usecase.overview.GetLastOverviewsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class DashboardViewModel(
    private val signOutUseCase: SignOutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getLastOverviewsUseCase: GetLastOverviewsUseCase,
) : ViewModel() {
    private val _accountUserState = MutableStateFlow<AccountUserState>(AccountUserState.None)
    val accountUserState = _accountUserState.asStateFlow()

    private val _lastOverviewsState: MutableStateFlow<LastOverviewsState> = MutableStateFlow(LastOverviewsState.None)
    val lastOverviewsState: StateFlow<LastOverviewsState> = _lastOverviewsState

    init {
        getCurrentUser()
        getLastOverviews()
    }

    private fun getCurrentUser() = viewModelScope.launch {
        _accountUserState.value = AccountUserState.Loading
        _accountUserState.value = getCurrentUserUseCase()
    }

    private fun getLastOverviews(size: Long = LAST_OVERVIEWS_COUNT) {
        _lastOverviewsState.value = LastOverviewsState.Loading

        viewModelScope.launch {
            try {
                val overviews = getLastOverviewsUseCase(size)

                _lastOverviewsState.value = LastOverviewsState.Success(overviews)
            } catch (e: Exception) {
                _lastOverviewsState.value = LastOverviewsState.None
            }
        }
    }

    fun signOut() = viewModelScope.launch {
        _accountUserState.value = AccountUserState.Loading
        _accountUserState.value = signOutUseCase()
    }
}

sealed class LastOverviewsState {
    data object None : LastOverviewsState()
    data object Loading : LastOverviewsState()
    data class Success(val overviews: List<ListItemOverviewModel>) : LastOverviewsState()
}

private const val LAST_OVERVIEWS_COUNT = 3L
