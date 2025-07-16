package com.example.FarmGame.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EquipmentModel(
    val itemId: String,
    var quantity: Int,
) : Parcelable