package com.example.FarmGame.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.FarmGame.R
import com.example.FarmGame.data.model.QuestModel
import com.example.FarmGame.data.model.QuestStatus
import com.example.FarmGame.domain.repository.QuestsStatusRepository
import com.example.FarmGame.presentation.qrScanner.QuestStatusState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.suspendCancellableCoroutine
import org.koin.core.annotation.Single
import java.io.IOException
import kotlin.coroutines.resume

@Single
class QuestsStatusRepositoryImpl(
    private val context: Context
) : QuestsStatusRepository {
    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private val gson: Gson by lazy { Gson() }

    override suspend fun getQuests(): List<QuestModel> =
        suspendCancellableCoroutine { continuation ->
            continuation.resume(
                try {
                    val inputStream = context.resources.openRawResource(R.raw.quests)
                    val jsonString = inputStream.bufferedReader().use { it.readText() }
                    val listType = object : TypeToken<List<QuestModel>>() {}.type

                    Gson().fromJson(jsonString, listType)
                } catch (e: IOException) {
                    e.printStackTrace()
                    emptyList()
                }
            )
        }

    override suspend fun getStatuses(): List<QuestStatus> =
        suspendCancellableCoroutine { continuation ->
            continuation.resume(getStoredQuestStatuses())
        }

    override suspend fun updateStatus(status: QuestStatus): QuestStatusState =
        suspendCancellableCoroutine { continuation ->
            val statuses = getStoredQuestStatuses().toMutableList()

            if (statuses.isNotEmpty()) {
                statuses[statuses.indexOfFirst { it == QuestStatus.STARTED }] = QuestStatus.FINISHED
            } else {
                statuses.add(QuestStatus.FINISHED)
            }

            statuses.add(status)

            saveQuestStatusToStorage(statuses)
            continuation.resume(QuestStatusState.Added)
        }

    override suspend fun clearStatuses(): QuestStatusState =
        suspendCancellableCoroutine { continuation ->
            val editor = sharedPreferences.edit()
            editor.remove(JSON_KEY)
            editor.apply()

            continuation.resume(QuestStatusState.Removed)
        }

    private fun getStoredQuestStatuses(): List<QuestStatus> {
        val json = sharedPreferences.getString(JSON_KEY, null) ?: return emptyList()
        return try {
            gson.fromJson(json, object : TypeToken<List<QuestStatus>>() {}.type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun saveQuestStatusToStorage(questStatuses: List<QuestStatus>) {
        sharedPreferences.edit()
            .putString(JSON_KEY, gson.toJson(questStatuses))
            .apply()
    }

    companion object {
        private const val PREFS_NAME = "farming_game"
        private const val JSON_KEY = "userQuestState"
    }
}