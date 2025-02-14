package com.example.pszzapp.data.model

import android.os.Parcelable
import com.example.pszzapp.presentation.hive.create.CreateHiveConstants
import com.example.pszzapp.presentation.hive.create.toFormattedDate
import com.example.pszzapp.presentation.overview.create.OverviewConstants
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Parcelize
data class HiveModel(
    val id: String,
    val uid: String,
    val apiaryId: String,
    val name: String,
    val familyType: Int,
    val hiveType: Int,
    val breed: Int,
    val line: String,
    val state: Int,
    val queenYear: Int,
    val queenAddedDate: LocalDate? = null,
    val hiveCreatedDate: LocalDate? = null,
    val queenNote: String,
) : Parcelable {
    constructor() : this(
        "", "", "", "", 2, 0, 0, "", 0, 0, null, null, "",
    )

    fun doesMatchSearchQuery(query: String): Boolean {
        val matchingCombinations = listOf(
            name,
        )

        return matchingCombinations.any {
            it.contains(query, ignoreCase = true)
        }
    }
}

@Parcelize
data class HiveInfoModel(
    val id: String,
    val uid: String,
    val name: String,
    val familyType: Int,
    val hiveType: Int,

    val breed: Int,
    val line: String,
    val state: Int,
    val queenYear: Int,

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
) : Parcelable

data class DetailedHiveInfoModel(
    val id: String,
//    val apiaryId: String,
//    val hiveId: String,
    val warningInfo: String? = null,
    val goodInfo: String? = null,
    val overviewDate: LocalDate? = null,
    val hiveInfoTiles: List<HiveInfoTile>,
)

data class HiveInfoTile(
    val title: String,
    val overviewItem: List<HiveInfoCell>,
    val disabled: Boolean = false,
)

data class HiveInfoCell(
    val key: String,
    val value: Int?,
    val stringValue: String? = null,
    val disabled: Boolean = false,
)


fun HiveInfoModel.toDetailedHiveInfoModel() = DetailedHiveInfoModel(
    id = id,
//    apiaryId = apiaryId,
//    hiveId = hiveId,
    warningInfo = if (cellType == 2) "Stan rojowy" else null,
    goodInfo = if (strength == 2) "Rodzina jest zdrowa i silna" else null,
    overviewDate = overviewDate,
    hiveInfoTiles = listOf(
        HiveInfoTile(
            title = "Informacje o rodzinie",
            overviewItem = listOf(
                HiveInfoCell(
                    key = "Siła",
                    value = OverviewConstants.strength[strength],
                ),
                HiveInfoCell(
                    key = "Nastrój",
                    value = OverviewConstants.mood[mood],
                ),
                HiveInfoCell(
                    key = "Czerw",
                    value = OverviewConstants.beeMaggots[beeMaggots],
                ),
                HiveInfoCell(
                    key = "Mateczniki - rodzaj",
                    value = OverviewConstants.cells[cellType],
                ),
                HiveInfoCell(
                    key = "Ilość pokarmu",
                    value = OverviewConstants.foodAmount[foodAmount],
                ),
                HiveInfoCell(
                    key = "Ramka pracy - data dodania/wymiany",
                    stringValue = workFrameDate?.toFormattedDate().toString(),
                    value = null,
                    disabled = workFrame == 0
                ),
            )
        ),
        HiveInfoTile(
            title = "Informacje o ulu",
            overviewItem = listOf(
                HiveInfoCell(
                    key = "Typ ula",
                    value = CreateHiveConstants.hiveType[hiveType]
                ),
                HiveInfoCell(
                    key = "Rodzaj rodziny",
                    value = CreateHiveConstants.familyType[familyType],
                ),
            )
        ),
        HiveInfoTile(
            title = "Informacje o matce",
            overviewItem = listOf(
                HiveInfoCell(
                    key = "Rasa",
                    value = CreateHiveConstants.breed[breed],
                ),
                HiveInfoCell(
                    key = "Linia",
                    stringValue = line,
                    value = null,
                    disabled = line == ""
                ),
                HiveInfoCell(
                    key = "Stan matki",
                    value = CreateHiveConstants.state[state],
                ),
                HiveInfoCell(
                    key = "Opalitek",
                    value = CreateHiveConstants.queenYear[queenYear],
                ),
            )
        ),
        HiveInfoTile(
            title = "Wyposażenie",
            overviewItem = listOf(
                HiveInfoCell(
                    key = "Krata odgrodowa",
                    value = OverviewConstants.partitionGrid[partitionGrid],
                ),
                HiveInfoCell(
                    key = "Izolator",
                    value = OverviewConstants.insulator[insulator],
                ),
                HiveInfoCell(
                    key = "Poławiacz pyłku",
                    value = OverviewConstants.pollenCatcher[pollenCatcher],
                ),
                HiveInfoCell(
                    key = "Poławiacz Propolisu",
                    value = OverviewConstants.propolisCatcher[propolisCatcher],
                ),
                HiveInfoCell(
                    key = "Miodnia - ilośc nadstawek",
                    stringValue = honeyWarehouseNumbers.toString(),
                    value = null,
                    disabled = honeyWarehouse == 0
                ),
            )
        ),
        HiveInfoTile(
            title = "Ostatnia notatka",
            disabled = true,
            overviewItem = listOf(
                HiveInfoCell(
                    key = "Lorem impsum dol sratataat",
                    value = OverviewConstants.beeMaggots[beeMaggots],
                ),
            )
        ),
    )
)