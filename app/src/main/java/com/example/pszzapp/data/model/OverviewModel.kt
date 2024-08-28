package com.example.pszzapp.data.model

import com.google.firebase.Timestamp
import java.time.LocalDate

data class OverviewModel(
    val id: String,
    val uid: String,
    val apiaryId: String,
    val hiveId: String,
    val strength: Int,
    val streets: Int,
    val mood: Int,
    val beeMaggots: Boolean,
    val cell: Boolean,
    val cellType: Int,
    val waxSheets: Int,
    val waxSheetsAdded: Int,
    val nestFrames: Int,
    val excluder: Boolean,
    val feeder: Boolean,
    val foodAmount: String,
    val insulator: Boolean,
    val pollenTrap: Boolean,
    val propolisTrap: Boolean,
    val honeyWarehouse: Boolean,
    val honeyFrames: Int,
    val workFrame: Boolean,
    val workFrameDate: LocalDate,
    val note: String,
    val overviewDate: LocalDate,
)