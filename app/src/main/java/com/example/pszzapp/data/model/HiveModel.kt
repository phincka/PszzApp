package com.example.pszzapp.data.model

import android.os.Parcelable
import com.example.pszzapp.data.model.ApiaryModel
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
        "", "", "", "", 0, 0, 0, "", 0, 0, null, null, "",
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
