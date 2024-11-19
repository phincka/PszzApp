package com.example.pszzapp.data.model

import android.os.Parcelable
import com.example.pszzapp.presentation.overview.create.OverviewConstants
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
    val workFrameDate: LocalDate? = LocalDate.now(),
    val overviewDate: LocalDate? = LocalDate.now(),
    val note: String,
): Parcelable {
    constructor(hiveId: String = "", apiaryId: String = "") : this(
        "", "", apiaryId, hiveId, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, LocalDate.now(), LocalDate.now(), "",
    )
}

data class ListItemOverviewModel(
    val id: String,
    val warningInfo: String? = null,
    val goodInfo: String? = null,
    val overviewDate: LocalDate,
)

data class DetailedOverviewModel(
    val id: String,
    val apiaryId: String,
    val hiveId: String,
    val warningInfo: String? = null,
    val goodInfo: String? = null,
    val overviewDate: LocalDate? = null,
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

fun OverviewModel.toDetailedOverviewModel() = DetailedOverviewModel(
    id = id,
    apiaryId = apiaryId,
    hiveId = hiveId,
    warningInfo = if (cellType == 2) "Stan rojowy" else null,
    goodInfo = if (strength == 2) "Rodzina jest zdrowa i silna" else null,
    overviewDate = overviewDate,
    overviewTiles = listOf(
        OverviewTile(
            title = "Informacje o rodzinie",
            overviewItem = listOf(
                OverviewCell(
                    key = "Siła",
                    value = OverviewConstants.strength[strength],
                ),
                OverviewCell(
                    key = "Nastrój",
                    value = OverviewConstants.mood[mood],
                ),
                OverviewCell(
                    key = "Czerw",
                    value = OverviewConstants.beeMaggots[beeMaggots],
                ),
            )
        ),
        OverviewTile(
            title = "Wyposażenie",
            overviewItem = listOf(
                OverviewCell(
                    key = "Podkarmiaczka",
                    value = OverviewConstants.beeMaggots[0],
                ),
                OverviewCell(
                    key = "Izolator",
                    value = OverviewConstants.insulator[insulator],
                ),
                OverviewCell(
                    key = "Poławiacz pyłku",
                    value = OverviewConstants.pollenCatcher[pollenCatcher],
                ),
                OverviewCell(
                    key = "Poławiacz Propolisu",
                    value = OverviewConstants.propolisCatcher[propolisCatcher],
                ),
            )
        ),
        OverviewTile(
            title = "Informacje o rodzinie",
            overviewItem = listOf(
                OverviewCell(
                    key = "Siła",
                    value = OverviewConstants.strength[strength],
                ),
                OverviewCell(
                    key = "Nastrój",
                    value = OverviewConstants.mood[mood],
                ),
                OverviewCell(
                    key = "Czerw",
                    value = OverviewConstants.beeMaggots[beeMaggots],
                ),
            )
        ),
    )
)