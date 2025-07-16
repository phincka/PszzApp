package com.example.FarmGame.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.FarmGame.R
import com.example.FarmGame.data.model.EquipmentModel
import com.example.FarmGame.data.model.GameItemModel
import com.example.FarmGame.data.model.QuestModel
import com.example.FarmGame.domain.repository.EquipmentRepository
import com.example.FarmGame.presentation.dashboard.EquipmentState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.suspendCancellableCoroutine
import org.koin.core.annotation.Single
import java.io.IOException
import kotlin.coroutines.resume

@Single
class EquipmentRepositoryImpl(
    private val context: Context
) : EquipmentRepository {

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private val gson: Gson by lazy { Gson() }

    override suspend fun getGameItems(): List<GameItemModel> =
        suspendCancellableCoroutine { continuation ->
            continuation.resume(
                try {
                    val inputStream = context.resources.openRawResource(R.raw.game_items)
                    val jsonString = inputStream.bufferedReader().use { it.readText() }
                    val listType = object : TypeToken<List<GameItemModel>>() {}.type

                    Gson().fromJson(jsonString, listType)
                } catch (e: IOException) {
                    e.printStackTrace()
                    emptyList()
                }
            )
        }

    override suspend fun loadEquipment(): List<EquipmentModel> = suspendCancellableCoroutine { continuation ->
        continuation.resume(getStoredEquipment())
    }

    override suspend fun addEquipmentItem(equipmentModel: EquipmentModel): EquipmentState =
        suspendCancellableCoroutine { continuation ->
            val equipment = getStoredEquipment().toMutableList()
            val index = equipment.indexOfFirst { it.itemId == equipmentModel.itemId }

            if (index != -1) {
                val updatedItem = equipment[index].copy(quantity = equipment[index].quantity + equipmentModel.quantity)
                equipment[index] = updatedItem
            } else if (equipmentModel.quantity > 0) {
                equipment.add(equipmentModel)
            }

            saveEquipmentToStorage(equipment)
            continuation.resume(EquipmentState.Added)
        }

    override suspend fun removeEquipmentItem(equipmentModel: EquipmentModel): EquipmentState =
        suspendCancellableCoroutine { continuation ->
            val equipment = getStoredEquipment().toMutableList()
            val index = equipment.indexOfFirst { it.itemId == equipmentModel.itemId }

            if (index != -1) {
                if (equipment[index].quantity == 1) {
                    equipment.removeAt(index)
                } else {
                    val updatedItem = equipment[index].copy(quantity = equipment[index].quantity - equipmentModel.quantity)
                    equipment[index] = updatedItem
                }
            } else if (equipmentModel.quantity >= 0) {
                equipment.remove(equipmentModel)
            }

            saveEquipmentToStorage(equipment)
            continuation.resume(EquipmentState.Removed)
        }

    override suspend fun clearEquipment(): EquipmentState =
        suspendCancellableCoroutine { continuation ->
            val editor = sharedPreferences.edit()
            editor.remove(JSON_KEY)
            editor.apply()

            continuation.resume(EquipmentState.Removed)
        }


    private fun getStoredEquipment(): List<EquipmentModel> {
        val json = sharedPreferences.getString(JSON_KEY, null) ?: return emptyList()
        return try {
            gson.fromJson(json, object : TypeToken<List<EquipmentModel>>() {}.type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun saveEquipmentToStorage(equipment: List<EquipmentModel>) {
        sharedPreferences.edit()
            .putString(JSON_KEY, gson.toJson(equipment))
            .apply()
    }

    companion object {
        private const val PREFS_NAME = "farming_game"
        private const val JSON_KEY = "equipment"
    }
}
