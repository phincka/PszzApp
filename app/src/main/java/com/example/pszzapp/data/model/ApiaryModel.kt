package com.example.pszzapp.data.model
import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Parcelize
data class ApiaryModel(
    val id: String,
    val uid: String,
    val name: String,
    val type: Int,
    val location: String,
    val hivesCount: Int ?= null,
    val timestamp: Timestamp ?= null,
    val lastOverview: LocalDate?= null,
) : Parcelable
