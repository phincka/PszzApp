package com.example.FarmGame.presentation.apiaries

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.FarmGame.data.model.EquipmentModel
import com.example.FarmGame.data.model.GameItemModel
import com.example.FarmGame.data.model.QuestModel
import com.example.FarmGame.data.model.QuestStatus
import com.example.FarmGame.domain.usecase.apiary.AddEquipmentItemUseCase
import com.example.FarmGame.domain.usecase.apiary.GetGameItemsUseCase
import com.example.FarmGame.domain.usecase.apiary.LoadEquipmentUseCase
import com.example.FarmGame.domain.usecase.apiary.RemoveEquipmentItemUseCase
import com.example.FarmGame.domain.usecase.quests.ClearQuestsStatusesUseCase
import com.example.FarmGame.domain.usecase.quests.GetQuestUseCase
import com.example.FarmGame.domain.usecase.quests.GetQuestsStatusesUseCase
import com.example.FarmGame.domain.usecase.quests.UpdateQuestsStatusUseCase
import com.example.FarmGame.presentation.dashboard.EquipmentState
import com.example.FarmGame.presentation.qrScanner.QuestStatusState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class ApiariesViewModel(
    private val getGameItemsUseCase: GetGameItemsUseCase,
    private val getQuestUseCase: GetQuestUseCase,
    private val loadEquipmentUseCase: LoadEquipmentUseCase,
    private val addEquipmentUseCase: AddEquipmentItemUseCase,
    private val removeEquipmentItemUseCase: RemoveEquipmentItemUseCase,
    private val getStatusesUseCase: GetQuestsStatusesUseCase,
    private val updateStatusUseCase: UpdateQuestsStatusUseCase,
    private val clearStatusesUseCase: ClearQuestsStatusesUseCase,
) : ViewModel() {
    private val _gameItems = MutableStateFlow<List<GameItemModel>>(emptyList())
    val gameItems = _gameItems.asStateFlow()

    private val _quests = MutableStateFlow<List<QuestModel>>(emptyList())
    val quests = _quests.asStateFlow()

    private val _equipmentState = MutableStateFlow<EquipmentState>(EquipmentState.None)
    val equipmentState = _equipmentState.asStateFlow()

    private val _equipment = MutableStateFlow<List<EquipmentModel>>(emptyList())
    val equipment = _equipment.asStateFlow()

    private val _questStatusState = MutableStateFlow<QuestStatusState>(QuestStatusState.None)
    val questStatusState = _questStatusState.asStateFlow()

    private val _questStatuses = MutableStateFlow<List<QuestStatus>>(emptyList())
    val questStatuses = _questStatuses.asStateFlow()

    init {
        getGameItems()
        getQuests()
        loadEquipment()
        getQuestStatuses()
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

    private fun getQuests() = viewModelScope.launch {
        Log.d("LOG_REL", "RELOAD_QUESTS")

        runCatching { getQuestUseCase() }
            .onSuccess { questsList ->
                _quests.value = questsList
            }
            .onFailure {
                _quests.value = emptyList()
            }
    }

    private fun loadEquipment() = viewModelScope.launch {
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

    private fun getQuestStatuses() = viewModelScope.launch {
        runCatching { getStatusesUseCase() }
            .onSuccess { equipmentList ->
                _questStatuses.value = equipmentList
            }
            .onFailure { error ->
                _questStatusState.value = QuestStatusState.Error(error.message.orEmpty())
            }
    }

    fun updateQuestStatus(questStatus: QuestStatus) = viewModelScope.launch {
        runCatching { updateStatusUseCase(status = questStatus) }
            .onSuccess { questStatusState ->
                getQuestStatuses()
                _questStatusState.value = questStatusState
            }
            .onFailure { error ->
                _questStatusState.value = QuestStatusState.Error(error.message.orEmpty())
            }
    }

    fun clearStatuses() = viewModelScope.launch {
        runCatching { clearStatusesUseCase() }
            .onSuccess { questStatusState ->
                getQuestStatuses()
                _questStatusState.value = questStatusState
            }
            .onFailure { error ->
                _questStatusState.value = QuestStatusState.Error(error.message.orEmpty())
            }
    }
}
