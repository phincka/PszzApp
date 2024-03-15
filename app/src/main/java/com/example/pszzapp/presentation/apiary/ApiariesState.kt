package com.example.pszzapp.presentation.apiary

import com.example.pszzapp.data.model.ApiaryModel

sealed class ApiariesState {
    data object Loading : ApiariesState()
    data class Success(val apiaries: List<ApiaryModel>) : ApiariesState()
    data class Error(val message: String) : ApiariesState()
}