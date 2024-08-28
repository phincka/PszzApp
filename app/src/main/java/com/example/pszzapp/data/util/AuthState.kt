package com.example.pszzapp.data.util

import com.example.pszzapp.data.model.UserModel

//data class SignInState(
//    val isSignInSuccessful: Boolean = false,
//    val signInError: String? = null
//)

sealed class AuthState {
    data object None : AuthState()

    data object Loading: AuthState()
    data object Success: AuthState()
    data class Error(val error: String): AuthState()
}

sealed class AccountUserState {
    data object None : AccountUserState()
    data object GuestState : AccountUserState()
    data object Loading: AccountUserState()
    data class SignedInState(val user: UserModel) : AccountUserState()
    data class Error(val message: String) : AccountUserState()
}