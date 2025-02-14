package com.example.pszzapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Parcelize
data class QueenModel(
    val id: String,
    val uid: String,
    val breed: Int,
    val line: String,
    val state: Int,
    val queenYear: Int,
    val queenAddedDate: LocalDate? = null,
    val queenNote: String,
) : Parcelable {
    constructor() : this(
        "", "", 0,  "", 0, 0, null, "",
    )
}
