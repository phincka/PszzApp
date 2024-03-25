package com.example.pszzapp.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pszzapp.data.model.UserModel
import com.example.pszzapp.data.util.AccountUserState
import com.example.pszzapp.domain.usecase.auth.GetCurrentUserUseCase
import com.example.pszzapp.domain.usecase.auth.SignOutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class DashboardViewModel(
    private val signOutUseCase: SignOutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
) : ViewModel() {
    private val _accountUserState = MutableStateFlow<AccountUserState>(AccountUserState.None)
    val accountUserState = _accountUserState.asStateFlow()

    init {
        getCurrentUser()
    }

    private fun getCurrentUser() = viewModelScope.launch {
        _accountUserState.value = AccountUserState.Loading
        _accountUserState.value = getCurrentUserUseCase()
    }

    fun signOut() = viewModelScope.launch {
        _accountUserState.value = AccountUserState.Loading
        _accountUserState.value = signOutUseCase()
    }
}