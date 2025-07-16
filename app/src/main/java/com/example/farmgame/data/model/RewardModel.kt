package com.example.FarmGame.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RewardModel(
    val itemId: String? = null,
    val quantity: Int = 1,
    val exp: Int? = null,
    val money: Int? = null,
) : Parcelable

