package com.example.FarmGame.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GameItemModel(
    val id: String,
    val name: String,
    val description: String,
    val price: Int,
    val itemType: ItemType,
    val growingTime: Int = 0,
    val yieldMultiplier: Int? = null,
    val expReward: Int? = null,
    val requiredLevel: Int,
 ) : Parcelable

enum class ItemType {
    SEED,
    CROP,
    ANIMAL,
    ANIMAL_PRODUCT,
    BOOSTER,
    TOOL,
    RECIPE,
}