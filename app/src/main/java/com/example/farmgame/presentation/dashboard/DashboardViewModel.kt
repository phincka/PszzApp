package com.example.FarmGame.presentation.dashboard

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.FarmGame.R
import com.example.FarmGame.data.model.EquipmentModel
import com.example.FarmGame.data.model.FieldModel
import com.example.FarmGame.data.model.GameItemModel
import com.example.FarmGame.domain.usecase.apiary.AddEquipmentItemUseCase
import com.example.FarmGame.domain.usecase.apiary.ClearEquipmentUseCase
import com.example.FarmGame.domain.usecase.apiary.GetGameItemsUseCase
import com.example.FarmGame.domain.usecase.apiary.LoadEquipmentUseCase
import com.example.FarmGame.domain.usecase.apiary.RemoveEquipmentItemUseCase
import com.example.FarmGame.domain.usecase.field.AddFieldUseCase
import com.example.FarmGame.domain.usecase.field.LoadFieldsUseCase
import com.example.FarmGame.domain.usecase.field.RemoveFieldUseCase
import com.example.FarmGame.domain.usecase.field.UpdateFieldUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class DashboardViewModel(
    private val getGameItemsUseCase: GetGameItemsUseCase,
    private val loadFieldsUseCase: LoadFieldsUseCase,
    private val addFieldUseCase: AddFieldUseCase,
    private val updateFieldUseCase: UpdateFieldUseCase,
    private val removeFieldUseCase: RemoveFieldUseCase,
    private val loadEquipmentUseCase: LoadEquipmentUseCase,
    private val addEquipmentUseCase: AddEquipmentItemUseCase,
    private val removeEquipmentItemUseCase: RemoveEquipmentItemUseCase,
    private val clearEquipmentUseCase: ClearEquipmentUseCase,
) : ViewModel() {
    private val _gameItems = MutableStateFlow<List<GameItemModel>>(emptyList())
    val gameItems = _gameItems.asStateFlow()

    private val _equipmentState = MutableStateFlow<EquipmentState>(EquipmentState.None)
    val equipmentState = _equipmentState.asStateFlow()

    private val _fieldState = MutableStateFlow<FieldState>(FieldState.None)
    val fieldState = _fieldState.asStateFlow()

    private val _fields = MutableStateFlow<List<FieldModel>>(emptyList())
    val fields = _fields.asStateFlow()

    private val _equipment = MutableStateFlow<List<EquipmentModel>>(emptyList())
    val equipment = _equipment.asStateFlow()


    init {
        getGameItems()
        loadFields()
        loadEquipment()
    }

    fun onRefresh() {
        loadEquipment()
    }

    private fun getGameItems() = viewModelScope.launch {
        Log.d("LOG_REL", "RELOAD_GAME_ITEMS")

        runCatching { getGameItemsUseCase() }
            .onSuccess { gameItemsList ->
                _gameItems.value = gameItemsList
            }
            .onFailure {
                _gameItems.value = emptyList()
            }
    }

    private fun loadFields() = viewModelScope.launch {
        Log.d("LOG_REL", "RELOAD_FIELDS")

        runCatching { loadFieldsUseCase() }
            .onSuccess { equipmentList ->
                _fields.value = equipmentList
            }
            .onFailure { error ->
                _fieldState.value = FieldState.Error(error.message.orEmpty())
            }
    }

    fun addField(fieldModel: FieldModel) {
        viewModelScope.launch {
            runCatching {
                addFieldUseCase(fieldModel)
            }.onSuccess { fieldState ->
                loadFields()
                _fieldState.value = fieldState
            }.onFailure { error ->
                _fieldState.value = FieldState.Error(error.message.toString())
            }
        }
    }

    fun updateField(fieldModel: FieldModel) {
        viewModelScope.launch {
            runCatching {
                updateFieldUseCase(fieldModel)
            }.onSuccess { fieldState ->
                loadFields()
                _fieldState.value = fieldState
            }.onFailure { error ->
                _fieldState.value = FieldState.Error(error.message.toString())
            }
        }
    }

    fun removeField(fieldModel: FieldModel) {
        viewModelScope.launch {
            runCatching {
                removeFieldUseCase(fieldModel)
            }.onSuccess { fieldState ->
                loadFields()
                _fieldState.value = fieldState
            }.onFailure { error ->
                _fieldState.value = FieldState.Error(error.message.toString())
            }
        }
    }

    private fun loadEquipment() = viewModelScope.launch {
        Log.d("LOG_REL", "RELOAD_EQ")
        runCatching { loadEquipmentUseCase() }
            .onSuccess { equipmentList ->
                _equipment.value = equipmentList
            }
            .onFailure { error ->
                _equipmentState.value = EquipmentState.Error(error.message.orEmpty())
            }
    }

    fun addToEquipment(equipmentModel: EquipmentModel) {
        viewModelScope.launch {
            runCatching {
                addEquipmentUseCase(equipmentModel)
            }.onSuccess { equipmentState ->
                loadEquipment()
                _equipmentState.value = equipmentState
            }.onFailure { error ->
                _equipmentState.value = EquipmentState.Error(error.message.toString())
            }
        }
    }

    fun removeEquipmentItem(equipmentModel: EquipmentModel) {
        viewModelScope.launch {
            runCatching {
                removeEquipmentItemUseCase(equipmentModel)
            }.onSuccess { equipmentState ->
                loadEquipment()
                _equipmentState.value = equipmentState
            }.onFailure { error ->
                _equipmentState.value = EquipmentState.Error(error.message.toString())
            }
        }
    }

    fun clearEquipment() {
        viewModelScope.launch {
            runCatching {
                clearEquipmentUseCase()
            }.onSuccess { equipmentState ->
                loadEquipment()
                _equipmentState.value = equipmentState
            }.onFailure { error ->
                _equipmentState.value = EquipmentState.Error(error.message.toString())
            }
        }
    }
}

sealed class FieldState {
    data object None : FieldState()
    data object Added : FieldState()
    data object Removed : FieldState()
    data class Error(val message: String) : FieldState()
}


sealed class EquipmentState {
    data object None : EquipmentState()
    data object Added : EquipmentState()
    data object Removed : EquipmentState()
    data class Error(val message: String) : EquipmentState()
}

