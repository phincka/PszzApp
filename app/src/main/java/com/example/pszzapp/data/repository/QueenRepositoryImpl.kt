package com.example.pszzapp.data.repository

import android.content.Context
import com.example.pszzapp.R
import com.example.pszzapp.data.model.ApiaryModel
import com.example.pszzapp.data.model.HiveModel
import com.example.pszzapp.data.model.QueenModel
import com.example.pszzapp.domain.repository.QueenRepository
import com.example.pszzapp.presentation.apiary.create.CreateApiaryState
import com.example.pszzapp.presentation.hive.create.CreateHiveState
import com.example.pszzapp.presentation.queen.RemoveQueenState
import com.example.pszzapp.presentation.queen.create.CreateQueenState
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.suspendCancellableCoroutine
import org.koin.core.annotation.Single
import java.time.LocalDate
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Single
class QueenRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFireStore: FirebaseFirestore,
    private val context: Context
) : QueenRepository {
    override suspend fun getQueens(hiveId: String): List<QueenModel> = suspendCoroutine { continuation ->
        val queensList = mutableListOf<QueenModel>()

        firebaseAuth.currentUser?.let { user ->
            firebaseFireStore
                .collection("queens")
                .where(
                    Filter.or(
                        Filter.equalTo("hiveId", hiveId),
                    )
                )
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot.documents) {
                        val queenData = document.data
                        queenData?.let {
                            queensList.add(it.toQueenModel(
                                documentId = document.id,
                                currentUserUid = user.uid,
                            ))
                        }
                    }
                    continuation.resume(queensList.sortedBy { it.queenAddedDate })
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        } ?: continuation.resume(queensList)
    }

    override suspend fun getQueenById(queenId: String): QueenModel? = suspendCoroutine { continuation ->
        val apiaryList = mutableListOf<ApiaryModel>()
        val hiveTasks = mutableListOf<Task<QuerySnapshot>>()

        firebaseAuth.currentUser?.let { currentUser ->
            firebaseFireStore
                .collection("queens")
                .where(
                    Filter.or(
                        Filter.equalTo("uid", currentUser.uid),
                    )
                )
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot.documents) {
                        val apiaryData = document.data
                        apiaryData?.let { data ->
                            var apiary = ApiaryModel(
                                id = document.id,
                                uid = currentUser.uid,
                                name = data["name"] as? String ?: "",
                                type = (data["type"] as? Number)?.toInt() ?: 0,
                                location = data["location"] as? String ?: "",
                                timestamp = data["timestamp"] as? Timestamp ?: Timestamp.now(),
                            )

                            val hiveTask = FirebaseFirestore.getInstance()
                                .collection("hives")
                                .whereEqualTo("apiaryId", document.id)
                                .get()
                                .addOnSuccessListener { hiveSnapshot ->
                                    apiary = apiary.copy(hivesCount = hiveSnapshot.documents.size)
                                    apiaryList.add(apiary)
                                }

                            hiveTasks.add(hiveTask)
                        }
                    }

//                    Tasks.whenAllComplete(hiveTasks)
//                        .addOnCompleteListener {
//                            continuation.resume(apiaryList.sortedBy { it.timestamp })
//                        }
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        } ?: continuation.resume(null)
    }

    override suspend fun createQueen(queenModel: QueenModel): CreateQueenState =
        suspendCancellableCoroutine { continuation ->
            firebaseAuth.currentUser?.let { user ->
                val docRef = firebaseFireStore
                    .collection("queens")
                    .document()
                val id = docRef.id

                val queen = QueenModel(
                    id = id,
                    uid = firebaseAuth.currentUser!!.uid,
                    breed = queenModel.breed,
                    line = queenModel.line,
                    state = queenModel.state,
                    queenYear = queenModel.queenYear,
                    queenAddedDate = queenModel.queenAddedDate,
                    queenNote = queenModel.queenNote
                )

                docRef.set(queen)
                continuation.resume(CreateQueenState.Redirect)
            } ?: continuation.resume(CreateQueenState.Error(context.getString(R.string.hive_state_no_user)))
        }

    override suspend fun editQueen(queenModel: QueenModel): CreateQueenState =
        suspendCancellableCoroutine { continuation ->
            firebaseAuth.currentUser?.let {
                firebaseFireStore
                    .collection("queens")
                    .document(queenModel.id)
                    .update(
                        mapOf(
                            "breed" to queenModel.breed,
                            "line" to queenModel.line,
                            "state" to queenModel.state,
                            "queenYear" to queenModel.queenYear,
                            "queenAddedDate" to queenModel.queenAddedDate,
                            "queenNote" to queenModel.queenNote,
                        ),
                    )

                continuation.resume(CreateQueenState.Redirect)
            } ?: continuation.resume(CreateQueenState.Error(context.getString(R.string.hive_state_no_user)))
        }

    override suspend fun removeQueen(
        queenId: String,
    ): RemoveQueenState =
        suspendCancellableCoroutine { continuation ->
            if (firebaseAuth.currentUser == null) continuation.resume(RemoveQueenState.Error("hive_state_no_user"))

            firebaseFireStore
                .collection("queens")
                .whereEqualTo("id", queenId)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        document.reference.delete()
                            .addOnFailureListener { e ->
                                continuation.resume(RemoveQueenState.Error("Error deleting document: $e"))
                            }
                    }

                    continuation.resume(RemoveQueenState.Success)
                }
                .addOnFailureListener {
                    continuation.resume(RemoveQueenState.Error("exception"))
                }
        }
}

private fun Map<String, Any>.toQueenModel(documentId: String, currentUserUid: String): QueenModel {
    return QueenModel(
        id = documentId,
        uid = currentUserUid,
        breed = this["breed"].toIntOrDefault(0),
        line = this["line"] as? String ?: "",
        state = this["state"].toIntOrDefault(0),
        queenYear = this["queenYear"].toIntOrDefault(0),
        queenAddedDate = getLocalDateFromFirestore(this, "queenAddedDate") ?: LocalDate.now(),
        queenNote = this["queenNote"] as? String ?: ""
    )
}