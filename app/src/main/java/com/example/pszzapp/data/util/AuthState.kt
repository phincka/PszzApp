package com.example.pszzapp.data.util

//data class SignInState(
//    val isSignInSuccessful: Boolean = false,
//    val signInError: String? = null
//)

sealed class AuthState {
    data object Loading: AuthState()

    data class Success(val success: Boolean , val message: String = ""): AuthState()

    data class Error(val error: String): AuthState()
}
