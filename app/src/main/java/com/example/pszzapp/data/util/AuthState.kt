package com.example.pszzapp.data.util

import com.example.pszzapp.data.model.UserModel

sealed class AuthState {
    data object None : AuthState()
    data object Loading: AuthState()

    data class Success(val success: Boolean , val message: String = ""): AuthState()

    data class Error(val message: String): AuthState()
}

sealed class AccountUserState {
    data object None : AccountUserState()
    data object GuestState : AccountUserState()
    data object Loading: AccountUserState()
    data class SignedInState(val user: UserModel) : AccountUserState()
    data class Error(val message: String) : AccountUserState()
}