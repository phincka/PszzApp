package com.example.pszzapp.data.repository

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.example.pszzapp.R
import com.example.pszzapp.data.model.ApiaryModel
import com.example.pszzapp.data.model.HiveModel
import com.example.pszzapp.domain.repository.ApiaryRepository
import com.example.pszzapp.domain.repository.HiveRepository
import com.example.pszzapp.presentation.apiary.RemoveApiaryState
import com.example.pszzapp.presentation.apiary.create.CreateApiaryState
import com.example.pszzapp.presentation.hive.RemoveHiveState
import com.example.pszzapp.presentation.hive.create.CreateHiveState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import org.koin.core.annotation.Single
import java.time.LocalDate
import kotlin.String
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Single
class HiveRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFireStore: FirebaseFirestore,
    private val context: Context
) : HiveRepository {
    override suspend fun getHivesByApiaryId(id: String): List<HiveModel> =
        suspendCoroutine { continuation ->
            val hivesList = mutableListOf<HiveModel>()

            firebaseAuth.currentUser?.let {
                firebaseFireStore
                    .collection("hives")
                    .whereEqualTo("apiaryId", id)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        for (document in querySnapshot.documents) {
                            val apiaryData = document.data

                            apiaryData?.let { data ->
                                val hive = data.toHiveModel(
                                    documentId = document.id,
                                    currentUserUid = it.uid,
                                )

                                hivesList.add(hive)
                            }
                        }
                        continuation.resume(hivesList.sortedBy { it.hiveCreatedDate })
                    }
                    .addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
            } ?: continuation.resume(hivesList)
        }

    override suspend fun getHiveById(id: String): HiveModel? =
        withTimeoutOrNull(10000L) {
            suspendCoroutine { continuation ->
                firebaseAuth.currentUser?.let {
                    firebaseFireStore
                        .collection("hives")
                        .document(id)
                        .get()
                        .addOnSuccessListener { documentSnapshot ->
                            val hiveData = documentSnapshot.data

                            hiveData?.let { data ->
                                val hive = data.toHiveModel(
                                    documentId = id,
                                    currentUserUid = it.uid,
                                )

                                continuation.resume(hive)
                            } ?: continuation.resume(null)
                        }
                        .addOnFailureListener { exception ->
                            continuation.resumeWithException(exception)
                        }
                } ?: continuation.resume(null)
            }
        }

    override suspend fun createHive(hiveModel: HiveModel): CreateHiveState =
        suspendCancellableCoroutine { continuation ->
            if (firebaseAuth.currentUser != null) {
                val docRef = firebaseFireStore
                    .collection("hives")
                    .document()
                val hiveId = docRef.id

                val hive = HiveModel(
                    id = hiveId,
                    uid = firebaseAuth.currentUser!!.uid,
                    apiaryId = hiveModel.apiaryId,
                    name = hiveModel.name,
                    familyType = hiveModel.familyType,
                    hiveType = hiveModel.hiveType,
                    breed = hiveModel.breed,
                    line = hiveModel.line,
                    state = hiveModel.state,
                    queenYear = hiveModel.queenYear,
                    queenAddedDate = hiveModel.queenAddedDate,
                    hiveCreatedDate = hiveModel.hiveCreatedDate,
                    queenNote = hiveModel.queenNote
                )

                docRef.set(hive)
                continuation.resume(CreateHiveState.Redirect(hiveId, apiaryId = hive.apiaryId))
            } else {
                continuation.resume(CreateHiveState.Error(context.getString(R.string.hive_state_no_user)))
            }
        }

    override suspend fun editHive(hiveModel: HiveModel): CreateHiveState =
        suspendCancellableCoroutine { continuation ->
            if (firebaseAuth.currentUser != null) {
                firebaseAuth.currentUser?.let {
                    firebaseFireStore
                        .collection("hives")
                        .document(hiveModel.id)
                        .update(
                            mapOf(
                                "name" to hiveModel.name,
                                "familyType" to hiveModel.familyType,
                                "hiveType" to hiveModel.hiveType,
                                "breed" to hiveModel.breed,
                                "line" to hiveModel.line,
                                "state" to hiveModel.state,
                                "queenYear" to hiveModel.queenYear,
                                "queenAddedDate" to hiveModel.queenAddedDate,
                                "hiveCreatedDate" to hiveModel.hiveCreatedDate,
                                "queenNote" to hiveModel.queenNote,
                            ),
                        )

                    continuation.resume(CreateHiveState.Redirect(hiveId = hiveModel.id, hiveModel.apiaryId))
                }
            } else {
                continuation.resume(CreateHiveState.Error("hive_state_no_user"))
            }
        }

    override suspend fun removeHive(
        hiveId: String,
    ): RemoveHiveState =
        suspendCancellableCoroutine { continuation ->
            if (firebaseAuth.currentUser != null) {
                firebaseAuth.currentUser?.let { currentUser ->
                    firebaseFireStore
                        .collection("hives")
                        .document(hiveId)
                        .delete()
                        .addOnSuccessListener {
                            firebaseFireStore
                                .collection("overviews")
                                .whereEqualTo("hiveId", hiveId)
                                .get()
                                .addOnSuccessListener { documents ->
                                    for (document in documents) {
                                        document.reference.delete()
                                            .addOnFailureListener { e ->
                                                continuation.resume(RemoveHiveState.Error("Error deleting document: $e"))
                                            }
                                    }
                                }
                                .addOnFailureListener {
                                    continuation.resume(RemoveHiveState.Error("exception"))
                                }

                            continuation.resume(RemoveHiveState.Success)
                        }
                        .addOnFailureListener {
                            continuation.resume(RemoveHiveState.Error("exception"))
                        }
                }
            } else {
                continuation.resume(RemoveHiveState.Error("hive_state_no_user"))
            }
        }
}

private fun Map<String, Any>.toHiveModel(documentId: String, currentUserUid: String): HiveModel {
    return HiveModel(
        id = documentId,
        uid = currentUserUid,
        apiaryId = this["apiaryId"] as? String ?: "",
        name = this["name"] as? String ?: "",
        familyType = this["familyType"].toIntOrDefault(0),
        hiveType = this["hiveType"].toIntOrDefault(0),
        breed = this["breed"].toIntOrDefault(0),
        line = this["line"] as? String ?: "",
        state = this["state"].toIntOrDefault(0),
        queenYear = this["queenYear"].toIntOrDefault(0),
        queenAddedDate = getLocalDateFromFirestore(this, "queenAddedDate") ?: LocalDate.now(),
        hiveCreatedDate = getLocalDateFromFirestore(this, "hiveCreatedDate") ?: LocalDate.now(),
        queenNote = this["queenNote"] as? String ?: ""
    )
}
