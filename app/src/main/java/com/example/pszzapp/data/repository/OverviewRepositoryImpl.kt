package com.example.pszzapp.data.repository

import android.content.Context
import com.example.pszzapp.R
import com.example.pszzapp.data.model.DetailedOverviewModel
import com.example.pszzapp.data.model.ListItemOverviewModel
import com.example.pszzapp.data.model.OverviewCell
import com.example.pszzapp.data.model.OverviewModel
import com.example.pszzapp.data.model.OverviewTile
import com.example.pszzapp.domain.repository.OverviewRepository
import com.example.pszzapp.presentation.overview.create.CreateOverviewState
import com.example.pszzapp.presentation.overview.create.OverviewConstants
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
                        continuation.resume(overviewsList)
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
                        continuation.resume(overviewsList)
                    }
                    .addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
            } ?: continuation.resume(overviewsList)
        }

    override suspend fun getLastOverviewId(hiveId: String): String? =
        suspendCoroutine { continuation ->
            firebaseAuth.currentUser?.let {
                firebaseFireStore
                    .collection("overviews")
                    .whereEqualTo("hiveId", hiveId)
                    .get()
                    .addOnSuccessListener { documentSnapshot ->
                        var overviewId: String? = null
                        if (documentSnapshot.documents.isNotEmpty()) overviewId =
                            documentSnapshot.documents.first().id
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

    override suspend fun getDetailedOverviewById(overviewId: String): DetailedOverviewModel? =
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

                            val overviewTiles = listOf(
                                OverviewTile(
                                    title = "Informacje o rodzinie",
                                    overviewItem = listOf(
                                        OverviewCell(
                                            key = "Siła",
                                            value = OverviewConstants.strength[overview.strength],
                                        ),
                                        OverviewCell(
                                            key = "Nastrój",
                                            value = OverviewConstants.mood[overview.mood],
                                        ),
                                        OverviewCell(
                                            key = "Czerw",
                                            value = OverviewConstants.beeMaggots[overview.beeMaggots],
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
                                            value = OverviewConstants.insulator[overview.insulator],
                                        ),
                                        OverviewCell(
                                            key = "Poławiacz pyłku",
                                            value = OverviewConstants.pollenCatcher[overview.pollenCatcher],
                                        ),
                                        OverviewCell(
                                            key = "Poławiacz Propolisu",
                                            value = OverviewConstants.propolisCatcher[overview.propolisCatcher],
                                        ),
                                    )
                                ),
                                OverviewTile(
                                    title = "Informacje o rodzinie",
                                    overviewItem = listOf(
                                        OverviewCell(
                                            key = "Siła",
                                            value = OverviewConstants.strength[overview.strength],
                                        ),
                                        OverviewCell(
                                            key = "Nastrój",
                                            value = OverviewConstants.mood[overview.mood],
                                        ),
                                        OverviewCell(
                                            key = "Czerw",
                                            value = OverviewConstants.beeMaggots[overview.beeMaggots],
                                        ),
                                    )
                                ),
                            )

                            val detailedOverview = DetailedOverviewModel(
                                id = overview.id,
                                hiveId = overview.hiveId,
                                warningInfo = if (overview.cellType == 2) "Stan rojowy" else null,
                                goodInfo = if (overview.strength == 2) "Rodzina jest zdrowa i silna" else null,
                                overviewDate = overview.overviewDate,
                                overviewTiles = overviewTiles
                            )

                            continuation.resume(detailedOverview)
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
}


private fun Map<String, Any>.toOverviewModel(): OverviewModel {
    return OverviewModel(
        id = this["id"] as? String ?: "",
        uid = this["uid"] as? String ?: "",
        apiaryId = this["apiaryId"] as? String ?: "",
        hiveId = this["hiveId"] as? String ?: "",
        strength = this["strength"] as? Int ?: 0,
        mood = this["mood"] as? Int ?: 0,
        beeMaggots = this["beeMaggots"] as? Int ?: 0,
        cellType = this["cellType"] as? Int ?: 0,
        partitionGrid = this["partitionGrid"] as? Int ?: 0,
        insulator = this["insulator"] as? Int ?: 0,
        pollenCatcher = this["pollenCatcher"] as? Int ?: 0,
        propolisCatcher = this["propolisCatcher"] as? Int ?: 0,
        honeyWarehouse = this["honeyWarehouse"] as? Int ?: 0,
        honeyWarehouseNumbers = this["honeyWarehouseNumbers"] as? Int ?: 0,
        foodAmount = this["foodAmount"] as? Int ?: 0,
        workFrame = this["workFrame"] as? Int ?: 0,
        workFrameDate = this["workFrameDate"] as? LocalDate ?: LocalDate.now(),
        overviewDate = this["overviewDate"] as? LocalDate ?: LocalDate.now(),
        note = this["note"] as? String ?: ""
    )
}

private fun Map<String, Any>.toListItemOverviewModel(): ListItemOverviewModel {
    return ListItemOverviewModel(
        id = this["id"] as? String ?: "",
        overviewDate = this["overviewDate"] as? LocalDate ?: LocalDate.now(),
    )
}