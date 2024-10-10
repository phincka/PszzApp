package com.example.pszzapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Parcelize
data class FeedingModel(
    val id: String,
    val uid: String,
    val apiaryId: String,
    val hiveId: String,
    val concentration: String,
    val syrupAmount: String,
    val sugarAmount: String,
    val waterAmount: String,
    val cakeAmount: String,
    val feedingDate: LocalDate,
) : Parcelable

@Parcelize
data class CreateFeedingModel(
    val id: String,
    val uid: String,
    val apiaryId: String,
    val hiveId: String,
    val concentration: String,
    val syrupAmount: String,
    val sugarAmount: String,
    val waterAmount: String,
    val cakeAmount: String,
    val feedingDate: LocalDate,
) : Parcelable
