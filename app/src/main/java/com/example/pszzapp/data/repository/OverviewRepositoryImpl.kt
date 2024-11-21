package com.example.pszzapp.data.repository

import android.content.Context
import com.example.pszzapp.R
import com.example.pszzapp.data.model.ListItemOverviewModel
import com.example.pszzapp.data.model.OverviewModel
import com.example.pszzapp.domain.repository.OverviewRepository
import com.example.pszzapp.presentation.overview.RemoveOverviewState
import com.example.pszzapp.presentation.overview.create.CreateOverviewState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.suspendCancellableCoroutine
import org.koin.core.annotation.Single
import java.time.LocalDate
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Single
class OverviewRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFireStore: FirebaseFirestore,
    private val context: Context
) : OverviewRepository {
    override suspend fun getOverviewsByHiveId(hiveId: String): List<ListItemOverviewModel> =
        suspendCoroutine { continuation ->
            val overviewsList = mutableListOf<ListItemOverviewModel>()

            firebaseAuth.currentUser?.let {
                firebaseFireStore
                    .collection("overviews")
                    .whereEqualTo("hiveId", hiveId)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        for (document in querySnapshot.documents) {
                            val overviewData = document.data

                            overviewData?.let {
                                var overview = overviewData.toListItemOverviewModel()
                                overview = overview.copy(warningInfo = "Testowa!")
                                overviewsList.add(overview)
                            }
                        }
                        continuation.resume(overviewsList.sortedBy { it.overviewDate })
                    }
                    .addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
            } ?: continuation.resume(overviewsList)
        }

    override suspend fun getLastOverviews(size: Long): List<ListItemOverviewModel> =
        suspendCoroutine { continuation ->
            val overviewsList = mutableListOf<ListItemOverviewModel>()

            firebaseAuth.currentUser?.let { currentUser ->
                firebaseFireStore
                    .collection("overviews")
                    .whereEqualTo("uid", currentUser.uid)
                    .limit(size)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        for (document in querySnapshot.documents) {
                            val overviewData = document.data

                            overviewData?.let {
                                var overview = overviewData.toListItemOverviewModel()
                                overview = overview.copy(warningInfo = "Testowa!")
                                overviewsList.add(overview)
                            }
                        }
                        continuation.resume(overviewsList.sortedByDescending { it.overviewDate })
                    }
                    .addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
            } ?: continuation.resume(overviewsList)
        }

    override suspend fun getLastOverviewId(hiveId: String): String? =
        suspendCoroutine { continuation ->
            val overviewsList = mutableListOf<ListItemOverviewModel>()

            firebaseAuth.currentUser?.let {
                firebaseFireStore
                    .collection("overviews")
                    .whereEqualTo("hiveId", hiveId)
                    .get()
                    .addOnSuccessListener { documentSnapshot ->
                        var overviewId: String? = null

                        for (document in documentSnapshot.documents) {
                            val overviewData = document.data

                            overviewData?.let {
                                var overview = overviewData.toListItemOverviewModel()
                                overview = overview.copy(warningInfo = "Testowa!")
                                overviewsList.add(overview)
                            }
                        }

                        if (overviewsList.isNotEmpty()) {
                            overviewId = overviewsList.sortedBy { it.overviewDate }.first().id
                        }

                        continuation.resume(overviewId)
                    }
                    .addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
            } ?: continuation.resume(null)
        }

    override suspend fun getOverviewById(overviewId: String): OverviewModel? =
        suspendCoroutine { continuation ->
            firebaseAuth.currentUser?.let {
                firebaseFireStore
                    .collection("overviews")
                    .document(overviewId)
                    .get()
                    .addOnSuccessListener { documentSnapshot ->
                        val overviewData = documentSnapshot.data

                        overviewData?.let {
                            val overview = overviewData.toOverviewModel()
                            continuation.resume(overview)
                        }
                    }
                    .addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
            } ?: continuation.resume(null)
        }

    override suspend fun createOverview(overviewModel: OverviewModel): CreateOverviewState =
        suspendCancellableCoroutine { continuation ->
            if (firebaseAuth.currentUser != null) {
                val docRef = firebaseFireStore
                    .collection("overviews")
                    .document()
                val overviewId = docRef.id

                val overview = OverviewModel(
                    id = overviewId,
                    uid = firebaseAuth.currentUser!!.uid,
                    apiaryId = overviewModel.apiaryId,
                    hiveId = overviewModel.hiveId,
                    strength = overviewModel.strength,
                    mood = overviewModel.mood,
                    beeMaggots = overviewModel.beeMaggots,
                    cellType = overviewModel.cellType,
                    partitionGrid = overviewModel.partitionGrid,
                    foodAmount = overviewModel.foodAmount,
                    insulator = overviewModel.insulator,
                    pollenCatcher = overviewModel.pollenCatcher,
                    propolisCatcher = overviewModel.propolisCatcher,
                    honeyWarehouse = overviewModel.honeyWarehouse,
                    honeyWarehouseNumbers = overviewModel.honeyWarehouseNumbers,
                    workFrame = overviewModel.workFrame,
                    workFrameDate = overviewModel.workFrameDate,
                    overviewDate = overviewModel.overviewDate,
                    note = overviewModel.note,
                )


                docRef.set(overview)
                continuation.resume(CreateOverviewState.Redirect(overviewId))
            } else {
                continuation.resume(CreateOverviewState.Error(context.getString(R.string.hive_state_no_user)))
            }
        }

    override suspend fun editOverview(overviewModel: OverviewModel): CreateOverviewState =
        suspendCancellableCoroutine { continuation ->
            if (firebaseAuth.currentUser != null) {
                firebaseAuth.currentUser?.let {
                    firebaseFireStore
                        .collection("overviews")
                        .document(overviewModel.id)
                        .update(
                            mapOf(
                                "strength" to overviewModel.strength,
                                "mood" to overviewModel.mood,
                                "beeMaggots" to overviewModel.beeMaggots,
                                "cellType" to overviewModel.cellType,
                                "insulator" to overviewModel.partitionGrid,
                                "insulator" to overviewModel.insulator,
                                "pollenCatcher" to overviewModel.pollenCatcher,
                                "propolisCatcher" to overviewModel.propolisCatcher,
                                "honeyWarehouse" to overviewModel.honeyWarehouse,
                                "honeyWarehouseNumbers" to overviewModel.honeyWarehouseNumbers,
                                "foodAmount" to overviewModel.foodAmount,
                                "workFrame" to overviewModel.workFrame,
                                "workFrameDate" to overviewModel.workFrameDate,
                                "overviewDate" to overviewModel.overviewDate,
                                "workFrameDate" to overviewModel.workFrameDate,
                            ),
                        )

                    continuation.resume(CreateOverviewState.Redirect(overviewId = overviewModel.id))
                }
            } else {
                continuation.resume(CreateOverviewState.Error("hive_state_no_user"))
            }
        }

    override suspend fun removeOverview(
        overviewId: String,
        hiveId: String,
    ): RemoveOverviewState =
        suspendCancellableCoroutine { continuation ->
            if (firebaseAuth.currentUser != null) {
                firebaseAuth.currentUser?.let {
                    firebaseFireStore
                        .collection("overviews")
                        .whereEqualTo("id", overviewId)
                        .get()
                        .addOnSuccessListener { documents ->
                            for (document in documents) {
                                document.reference.delete()
                                    .addOnFailureListener { e ->
                                        continuation.resume(RemoveOverviewState.Error("Error deleting document: $e"))
                                    }
                            }

                            continuation.resume(RemoveOverviewState.Success(hiveId = hiveId))
                        }
                        .addOnFailureListener {
                            continuation.resume(RemoveOverviewState.Error("exception"))
                        }
                }
            } else {
                continuation.resume(RemoveOverviewState.Error("hive_state_no_user"))
            }
        }
}


private fun Map<String, Any>.toOverviewModel(): OverviewModel {
    return OverviewModel(
        id = this["id"] as? String ?: "",
        uid = this["uid"] as? String ?: "",
        apiaryId = this["apiaryId"] as? String ?: "",
        hiveId = this["hiveId"] as? String ?: "",
        strength = this["strength"].toIntOrDefault(0),
        mood = this["mood"].toIntOrDefault(0),
        beeMaggots = this["beeMaggots"].toIntOrDefault(0),
        cellType = this["cellType"].toIntOrDefault(0),
        partitionGrid = this["partitionGrid"].toIntOrDefault(0),
        insulator = this["insulator"].toIntOrDefault(0),
        pollenCatcher = this["pollenCatcher"].toIntOrDefault(0),
        propolisCatcher = this["propolisCatcher"].toIntOrDefault(0),
        honeyWarehouse = this["honeyWarehouse"].toIntOrDefault(0),
        honeyWarehouseNumbers = this["honeyWarehouseNumbers"].toIntOrDefault(0),
        foodAmount = this["foodAmount"].toIntOrDefault(0),
        workFrame = this["workFrame"].toIntOrDefault(0),
        workFrameDate = getLocalDateFromFirestore(this, "workFrameDate"),
        overviewDate = getLocalDateFromFirestore(this, "overviewDate"),
        note = this["note"] as? String ?: ""
    )
}

private fun Map<String, Any>.toListItemOverviewModel(): ListItemOverviewModel {
    return ListItemOverviewModel(
        id = this["id"] as? String ?: "",
        overviewDate = getLocalDateFromFirestore(this, "overviewDate"),
    )
}

fun getLocalDateFromFirestore(data: Map<String, Any>, key: String): LocalDate {
    val dateMap = data[key] as? Map<String, Any>
    return if (dateMap != null) {
        val year = (dateMap["year"] as? Number)?.toInt() ?: 0
        val month = (dateMap["monthValue"] as? Number)?.toInt() ?: 1
        val day = (dateMap["dayOfMonth"] as? Number)?.toInt() ?: 1

        LocalDate.of(year, month, day)
    } else {
        LocalDate.now()
    }
}

fun Any?.toIntOrDefault(default: Int): Int {
    return when (this) {
        is Int -> this
        is Long -> this.toInt()
        is Double -> this.toInt()
        else -> default
    }
}