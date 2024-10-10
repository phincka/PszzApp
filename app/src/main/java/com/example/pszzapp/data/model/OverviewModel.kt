package com.example.pszzapp.data.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Parcelize
data class OverviewModel(
    val id: String,
    val uid: String,
    val apiaryId: String,
    val hiveId: String,
    val strength: Int,
    val mood: Int,
    val beeMaggots: Int,
    val cellType: Int,
    val partitionGrid: Int,
    val insulator: Int,
    val pollenCatcher: Int,
    val propolisCatcher: Int,
    val honeyWarehouse: Int,
    val honeyWarehouseNumbers: Int,
    val foodAmount: Int,
    val workFrame: Int,
    val workFrameDate: LocalDate,
    val overviewDate: LocalDate,
    val note: String,
): Parcelable

data class ListItemOverviewModel(
    val id: String,
    val warningInfo: String? = null,
    val goodInfo: String? = null,
    val overviewDate: LocalDate,
)

data class DetailedOverviewModel(
    val id: String,
    val hiveId: String,
    val warningInfo: String? = null,
    val goodInfo: String? = null,
    val overviewDate: LocalDate,
    val overviewTiles: List<OverviewTile>,
)

data class OverviewTile(
    val title: String,
    val overviewItem: List<OverviewCell>
)

data class OverviewCell(
    val key: String,
    val value: Int,
)