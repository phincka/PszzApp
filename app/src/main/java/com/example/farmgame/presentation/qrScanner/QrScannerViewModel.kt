package com.example.FarmGame.presentation.qrScanner

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.FarmGame.data.model.EquipmentModel
import com.example.FarmGame.data.model.GameItemModel
import com.example.FarmGame.data.model.QuestModel
import com.example.FarmGame.domain.usecase.apiary.AddEquipmentItemUseCase
import com.example.FarmGame.domain.usecase.apiary.GetGameItemsUseCase
import com.example.FarmGame.domain.usecase.apiary.LoadEquipmentUseCase
import com.example.FarmGame.domain.usecase.apiary.RemoveEquipmentItemUseCase
import com.example.FarmGame.domain.usecase.quests.GetQuestUseCase
import com.example.FarmGame.presentation.dashboard.EquipmentState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class QrScannerViewModel(
    private val getGameItemsUseCase: GetGameItemsUseCase,
    private val getQuestUseCase: GetQuestUseCase,
    private val loadEquipmentUseCase: LoadEquipmentUseCase,
    private val removeEquipmentItemUseCase: RemoveEquipmentItemUseCase,
    private val addEquipmentItemUseCase: AddEquipmentItemUseCase,
) : ViewModel() {
    private val _gameItems = MutableStateFlow<List<GameItemModel>>(emptyList())
    val gameItems = _gameItems.asStateFlow()

    private val _equipmentState = MutableStateFlow<EquipmentState>(EquipmentState.None)
    val equipmentState = _equipmentState.asStateFlow()

    private val _equipment = MutableStateFlow<List<EquipmentModel>>(emptyList())
    val equipment = _equipment.asStateFlow()


    init {
        getGameItems()
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

    fun addEquipmentItem(equipmentModel: EquipmentModel) {
        viewModelScope.launch {
            runCatching {
                addEquipmentItemUseCase(equipmentModel)
            }.onSuccess { equipmentState ->
                loadEquipment()
                _equipmentState.value = equipmentState
            }.onFailure { error ->
                _equipmentState.value = EquipmentState.Error(error.message.toString())
            }
        }
    }
}

sealed class QuestStatusState {
    data object None : QuestStatusState()
    data object Added : QuestStatusState()
    data object Removed : QuestStatusState()
    data class Error(val message: String) : QuestStatusState()
}
