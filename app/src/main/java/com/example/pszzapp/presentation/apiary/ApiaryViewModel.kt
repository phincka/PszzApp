package com.example.pszzapp.presentation.apiary

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pszzapp.data.model.ApiaryModel
import com.example.pszzapp.data.model.HiveModel
import com.example.pszzapp.domain.usecase.apiary.GetApiaryByIdUseCase
import com.example.pszzapp.domain.usecase.hive.GetHivesByApiaryIdUseCase
import com.example.pszzapp.domain.usecase.overview.GetOverviewsByHiveIdUseCase
import com.example.pszzapp.presentation.hive.HiveState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class ApiaryViewModel(
    id: String,
    private val getApiaryByIdUseCase: GetApiaryByIdUseCase,
    private val getHivesByApiaryIdUseCase: GetHivesByApiaryIdUseCase
) : ViewModel() {
    private val _apiaryState: MutableStateFlow<ApiaryState> = MutableStateFlow(ApiaryState.Loading)
    val apiaryState: StateFlow<ApiaryState> = _apiaryState

    init {
        getHivesByApiaryId(id)
    }

    private fun getHivesByApiaryId(id: String) {
        viewModelScope.launch {
            try {
                val apiary = getApiaryByIdUseCase(id)
                val hives = getHivesByApiaryIdUseCase(id)

                if (apiary != null) {
                    _apiaryState.value = ApiaryState.Success(
                        apiary = apiary,
                        hives = hives,
                    )
                } else {
                    _apiaryState.value = ApiaryState.Error("Failed: TODO")
                }

            } catch (e: Exception) {
                _apiaryState.value = ApiaryState.Error("Failed: ${e.message}")
            }
        }
    }



    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _persons = MutableStateFlow(
        if (apiaryState.value is ApiaryState.Success) {
            (apiaryState.value as ApiaryState.Success).hives
        } else {
            listOf<HiveModel>()
        }
    )


    @OptIn(FlowPreview::class)
    val persons = searchText
        .onEach { _isSearching.update { true } }
        .combine(_apiaryState) { text, apiaryState ->
            Log.d("LOG_a", apiaryState.toString())

            val hives = if (apiaryState is ApiaryState.Success) {
                apiaryState.hives
            } else {
                listOf<HiveModel>()
            }

            if(text.isBlank()) {
                hives
            } else {
                hives.filter {
                    it.doesMatchSearchQuery(text)
                }
            }
        }
        .onEach { _isSearching.update { false } }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList() // Initial state, when nothing is loaded yet
        )
    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }
}

sealed class ApiaryState {
    data object Loading : ApiaryState()
    data class Success(
        val apiary: ApiaryModel,
        val hives: List<HiveModel>
    ) : ApiaryState()
    data class Error(val message: String) : ApiaryState()
}