package com.example.FarmGame.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import com.example.FarmGame.data.model.FieldModel
import com.example.FarmGame.domain.repository.FieldsRepository
import com.example.FarmGame.presentation.dashboard.FieldState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.suspendCancellableCoroutine
import org.koin.core.annotation.Single
import kotlin.coroutines.resume

@Single
class FieldsRepositoryImpl(
    private val context: Context
) : FieldsRepository {
    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private val gson: Gson by lazy { Gson() }

    override suspend fun loadFields(): List<FieldModel> =
        suspendCancellableCoroutine { continuation ->
            continuation.resume(getStoredFields())
        }

    override suspend fun addField(fieldModel: FieldModel): FieldState =
        suspendCancellableCoroutine { continuation ->
            val fields = getStoredFields().toMutableList()

            fields.add(fieldModel)
            saveFieldsToStorage(fields)

            continuation.resume(FieldState.Added)
        }

    override suspend fun updateField(fieldModel: FieldModel): FieldState =
        suspendCancellableCoroutine { continuation ->
            updateFieldModel(fieldModel)

            continuation.resume(FieldState.Added)
        }

    override suspend fun removeField(fieldModel: FieldModel): FieldState =
        suspendCancellableCoroutine { continuation ->
            val fields = getStoredFields().toMutableList()

            fields.remove(
                fieldModel
            )
            saveFieldsToStorage(fields)

            continuation.resume(FieldState.Removed)
        }

    override suspend fun clearFields(): FieldState =
        suspendCancellableCoroutine { continuation ->
            val editor = sharedPreferences.edit()
            editor.remove(JSON_KEY)
            editor.apply()

            continuation.resume(FieldState.Removed)
        }


    private fun getStoredFields(): List<FieldModel> {
        val json = sharedPreferences.getString(JSON_KEY, null) ?: return emptyList()
        return try {
            gson.fromJson(json, object : TypeToken<List<FieldModel>>() {}.type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun updateFieldModel(fieldModel: FieldModel): Boolean {
        val json = sharedPreferences.getString(JSON_KEY, null) ?: return false

        return try {
            // Pobranie listy FieldModel
            val fieldList: MutableList<FieldModel> = gson.fromJson(json, object : TypeToken<List<FieldModel>>() {}.type) ?: mutableListOf()

            // Znajdź element na podstawie unikalnego identyfikatora (np. id)
            val index = fieldList.indexOfFirst { it.x == fieldModel.x && it.y == fieldModel.y }
            if (index != -1) {
                // Zaktualizuj istniejący element
                fieldList[index] = fieldModel

                // Zapisz zaktualizowaną listę do SharedPreferences
                val updatedJson = gson.toJson(fieldList)
                sharedPreferences.edit().putString(JSON_KEY, updatedJson).apply()

                true
            } else {
                // Element nie został znaleziony
                false
            }
        } catch (e: Exception) {
            // Obsługuje błędy parsowania lub inne wyjątki
            false
        }
    }

    private fun saveFieldsToStorage(fields: List<FieldModel>) {
        sharedPreferences.edit()
            .putString(JSON_KEY, gson.toJson(fields))
            .apply()
    }

    companion object {
        private const val PREFS_NAME = "farming_game"
        private const val JSON_KEY = "fields"
    }
}