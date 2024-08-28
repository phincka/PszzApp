package com.example.pszzapp.data.repository

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import com.example.pszzapp.R
import com.example.pszzapp.data.model.ApiaryModel
import com.example.pszzapp.data.model.HiveModel
import com.example.pszzapp.data.model.OverviewModel
import com.example.pszzapp.domain.repository.ApiaryRepository
import com.example.pszzapp.domain.repository.HiveRepository
import com.example.pszzapp.domain.repository.OverviewRepository
import com.example.pszzapp.presentation.apiary.create.CreateApiaryState
import com.example.pszzapp.presentation.hive.create.CreateHiveState
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
    override suspend fun getOverviewsByHiveId(hiveId: String): List<OverviewModel> =
        suspendCoroutine { continuation ->
            val overviewsList = mutableListOf<OverviewModel>()

            firebaseAuth.currentUser?.let { currentUser ->
                firebaseFireStore
                    .collection("pszzApp")
                    .document(currentUser.uid)
                    .collection("overviews")
                    .whereEqualTo("hiveId", hiveId)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        for (document in querySnapshot.documents) {
                            val overviewData = document.data

                            overviewData?.let { data ->
                                val overview = OverviewModel(
                                    id = document.id,
                                    uid = data["uid"] as? String ?: "",
                                    apiaryId = data["apiaryId"] as? String ?: "",
                                    hiveId = data["hiveId"] as? String ?: "",
                                    strength = data["strength"] as? Int ?: 0,
                                    mood = data["mood"] as? Int ?: 0,
                                    streets = data["streets"] as? Int ?: 0,
                                    beeMaggots = data["beeMaggots"] as? Boolean ?: false,
                                    cell = data["cell"] as? Boolean ?: false,
                                    cellType = data["cellType"] as? Int ?: 0,
                                    waxSheets = data["waxSheets"] as? Int ?: 0,
                                    waxSheetsAdded = data["waxSheetsAdded"] as? Int ?: 0,
                                    nestFrames = data["nestFrames"] as? Int ?: 0,
                                    excluder = data["excluder"] as? Boolean ?: false,
                                    feeder = data["feeder"] as? Boolean ?: false,
                                    foodAmount = data["foodAmount"] as? String ?: "",
                                    insulator = data["insulator"] as? Boolean ?: false,
                                    pollenTrap = data["pollenTrap"] as? Boolean ?: false,
                                    propolisTrap = data["propolisTrap"] as? Boolean ?: false,
                                    honeyWarehouse = data["honeyWarehouse"] as? Boolean ?: false,
                                    honeyFrames = data["honeyFrames"] as? Int ?: 0,
                                    workFrame = data["workFrame"] as? Boolean ?: false,
                                    workFrameDate = data["workFrameDate"] as? LocalDate?: LocalDate.now(),
                                    note = data["note"] as? String ?: "",
                                    overviewDate = data["overviewDate"] as? LocalDate?: LocalDate.now(),
                                )

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

//    override suspend fun getLastOverviews(): List<OverviewModel> =
//        suspendCoroutine { continuation ->
//            val overviewsList = mutableListOf<OverviewModel>()
//
//            firebaseAuth.currentUser?.let { currentUser ->
//                firebaseFireStore
//                    .collection("pszzApp")
////                    .where(
////                        Filter.or(
////                            Filter.equalTo("ownerId", currentUser.uid),
////                            Filter.arrayContains("sharedIds", currentUser.email)
////                        )
//                    .document(currentUser.uid)
//                    .collection("overviews")
//                    .whereEqualTo("hiveId", hiveId)
//                    .get()
//                    .addOnSuccessListener { querySnapshot ->
//                        for (document in querySnapshot.documents) {
//                            val overviewData = document.data
//
//                            overviewData?.let { data ->
//                                val overview = OverviewModel(
//                                    id = document.id,
//                                    uid = data["uid"] as? String ?: "",
//                                    apiaryId = data["apiaryId"] as? String ?: "",
//                                    hiveId = data["hiveId"] as? String ?: "",
//                                    strength = data["strength"] as? Int ?: 0,
//                                    mood = data["mood"] as? Int ?: 0,
//                                    streets = data["streets"] as? Int ?: 0,
//                                    beeMaggots = data["beeMaggots"] as? Boolean ?: false,
//                                    cell = data["cell"] as? Boolean ?: false,
//                                    cellType = data["cellType"] as? Int ?: 0,
//                                    waxSheets = data["waxSheets"] as? Int ?: 0,
//                                    waxSheetsAdded = data["waxSheetsAdded"] as? Int ?: 0,
//                                    nestFrames = data["nestFrames"] as? Int ?: 0,
//                                    excluder = data["excluder"] as? Boolean ?: false,
//                                    feeder = data["feeder"] as? Boolean ?: false,
//                                    foodAmount = data["foodAmount"] as? String ?: "",
//                                    insulator = data["insulator"] as? Boolean ?: false,
//                                    pollenTrap = data["pollenTrap"] as? Boolean ?: false,
//                                    propolisTrap = data["propolisTrap"] as? Boolean ?: false,
//                                    honeyWarehouse = data["honeyWarehouse"] as? Boolean ?: false,
//                                    honeyFrames = data["honeyFrames"] as? Int ?: 0,
//                                    workFrame = data["workFrame"] as? Boolean ?: false,
//                                    workFrameDate = data["workFrameDate"] as? LocalDate?: LocalDate.now(),
//                                    note = data["note"] as? String ?: "",
//                                    overviewDate = data["overviewDate"] as? LocalDate?: LocalDate.now(),
//                                )
//
//                                overviewsList.add(overview)
//                            }
//                        }
//                        continuation.resume(overviewsList)
//                    }
//                    .addOnFailureListener { exception ->
//                        continuation.resumeWithException(exception)
//                    }
//            } ?: continuation.resume(overviewsList)
//        }

    override suspend fun getOverviewById(overviewId: String): OverviewModel? =
        suspendCoroutine { continuation ->

            firebaseAuth.currentUser?.let { currentUser ->
                firebaseFireStore
                    .collection("pszzApp")
                    .document(firebaseAuth.currentUser!!.uid)
                    .collection("overviews")
                    .document(overviewId)
                    .get()
                    .addOnSuccessListener {documentSnapshot ->
                        val overviewData = documentSnapshot.data

                        overviewData?.let { data ->
                            val overview = OverviewModel(
                                id = overviewId,
                                uid = data["uid"] as? String ?: "",
                                apiaryId = data["apiaryId"] as? String ?: "",
                                hiveId = data["hiveId"] as? String ?: "",
                                strength = data["strength"] as? Int ?: 0,
                                mood = data["mood"] as? Int ?: 0,
                                streets = data["streets"] as? Int ?: 0,
                                beeMaggots = data["beeMaggots"] as? Boolean ?: false,
                                cell = data["cell"] as? Boolean ?: false,
                                cellType = data["cellType"] as? Int ?: 0,
                                waxSheets = data["waxSheets"] as? Int ?: 0,
                                waxSheetsAdded = data["waxSheetsAdded"] as? Int ?: 0,
                                nestFrames = data["nestFrames"] as? Int ?: 0,
                                excluder = data["excluder"] as? Boolean ?: false,
                                feeder = data["feeder"] as? Boolean ?: false,
                                foodAmount = data["foodAmount"] as? String ?: "",
                                insulator = data["insulator"] as? Boolean ?: false,
                                pollenTrap = data["pollenTrap"] as? Boolean ?: false,
                                propolisTrap = data["propolisTrap"] as? Boolean ?: false,
                                honeyWarehouse = data["honeyWarehouse"] as? Boolean ?: false,
                                honeyFrames = data["honeyFrames"] as? Int ?: 0,
                                workFrame = data["workFrame"] as? Boolean ?: false,
                                workFrameDate = data["workFrameDate"] as? LocalDate?: LocalDate.now(),
                                note = data["note"] as? String ?: "",
                                overviewDate = data["overviewDate"] as? LocalDate?: LocalDate.now(),
                            )

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
                val overview = OverviewModel(
                    id = "",
                    uid = firebaseAuth.currentUser!!.uid,
                    apiaryId = overviewModel.apiaryId,
                    hiveId = overviewModel.hiveId,
                    strength = overviewModel.strength,
                    mood = overviewModel.mood,
                    streets = overviewModel.streets,
                    beeMaggots = overviewModel.beeMaggots,
                    cell = overviewModel.cell,
                    cellType = overviewModel.cellType,
                    waxSheets = overviewModel.waxSheets,
                    waxSheetsAdded = overviewModel.waxSheetsAdded,
                    nestFrames = overviewModel.nestFrames,
                    excluder = overviewModel.excluder,
                    feeder = overviewModel.feeder,
                    foodAmount = overviewModel.foodAmount,
                    insulator = overviewModel.insulator,
                    pollenTrap = overviewModel.pollenTrap,
                    propolisTrap = overviewModel.propolisTrap,
                    honeyWarehouse = overviewModel.honeyWarehouse,
                    honeyFrames = overviewModel.honeyFrames,
                    workFrame = overviewModel.workFrame,
                    workFrameDate = overviewModel.workFrameDate,
                    note = overviewModel.note,
                    overviewDate = overviewModel.overviewDate
                )

                val docRef = firebaseFireStore
                    .collection("pszzApp")
                    .document(firebaseAuth.currentUser!!.uid)
                    .collection("overviews")
                    .document()
                val overviewId = docRef.id
                docRef.set(overview)

                continuation.resume(CreateOverviewState.Redirect(overviewId))
            } else {
                continuation.resume(CreateOverviewState.Error(context.getString(R.string.hive_state_no_user)))
            }
        }
}