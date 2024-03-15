package com.example.pszzapp.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pszzapp.data.model.UserModel
import com.example.pszzapp.data.util.AuthState
import com.example.pszzapp.domain.usecase.auth.GetCurrentUserUseCase
import com.example.pszzapp.domain.usecase.auth.SignOutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class MainViewModel(
    private val signOutUseCase: SignOutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
) : ViewModel() {
    private val _mainState: MutableStateFlow<MainState> = MutableStateFlow(MainState.Success(hives = false))
    val mainState: StateFlow<MainState> = _mainState

    private val _user = MutableStateFlow<UserModel?>(null)
    val user = _user.asStateFlow()

    private val _signOutState: MutableStateFlow<AuthState> = MutableStateFlow(AuthState.Success(false))
    val signOutState: StateFlow<AuthState> = _signOutState

    init {
        getCurrentUser()
    }

    private fun getCurrentUser() {
        viewModelScope.launch {
            try {
                val user = getCurrentUserUseCase()
                if (user != null) _user.value = user
            } catch (e: Exception) {
                _user.value = null
            }
        }
    }

    fun signOut() = viewModelScope.launch {
        _signOutState.value = AuthState.Loading
        _signOutState.value = signOutUseCase()
    }
}