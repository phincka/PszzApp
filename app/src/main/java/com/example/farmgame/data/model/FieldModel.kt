package com.example.FarmGame.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FieldModel(
    val title: String,
    val x: Int,
    val y: Int,
    val fieldType: FieldType,
    var status: Status,
    val actionDuration: Int,
    val timestamp: Long,
    val cropId: String? = null
) : Parcelable

enum class FieldType { FARMLAND, ANIMAL, FACTORY }

enum class Status { IDLE, IN_PROGRESS, READY_TO_HARVEST }