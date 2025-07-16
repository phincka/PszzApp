package com.example.FarmGame.data.model
import android.os.Parcelable
import com.google.gson.JsonSyntaxException
import kotlinx.parcelize.Parcelize

@Parcelize
data class QuestModel(
    val title: String,
    val description: String,
    val reward: RewardModel? = null,
    val requiredItems: EquipmentModel,
): Parcelable

enum class QuestStatus { NOT_STARTED, STARTED, FINISHED }

