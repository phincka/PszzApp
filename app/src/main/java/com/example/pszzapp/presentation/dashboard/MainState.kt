package com.example.pszzapp.presentation.dashboard

sealed class MainState {
    data object Loading : MainState()
    data class Success(val hives: Boolean) : MainState()
    data class Error(val message: String) : MainState()
}