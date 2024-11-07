package com.example.pszzapp.presentation.auth.signUp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pszzapp.data.util.AuthState
import com.example.pszzapp.domain.usecase.auth.SignUpUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class SignUpViewModel(
    private val signUpUseCase: SignUpUseCase,
) : ViewModel() {
    private val _signUpState: MutableStateFlow<AuthState> = MutableStateFlow(AuthState.None)
    val signUpState: StateFlow<AuthState> = _signUpState

    fun signUp(
        name: String,
        email: String,
        password: String,
        repeatPassword: String,
    ) {
        viewModelScope.launch {
            _signUpState.value = AuthState.Loading
            _signUpState.value = signUpUseCase(name, email, password, repeatPassword)
        }
    }
}