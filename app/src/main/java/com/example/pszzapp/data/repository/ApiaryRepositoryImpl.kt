package com.example.pszzapp.data.repository

import android.content.Context
import com.example.pszzapp.R
import com.example.pszzapp.data.model.ApiaryModel
import com.example.pszzapp.domain.repository.ApiaryRepository
import com.example.pszzapp.presentation.apiary.RemoveApiaryState
import com.example.pszzapp.presentation.apiary.create.CreateApiaryState
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.suspendCancellableCoroutine
import org.koin.core.annotation.Single
import java.time.LocalDate
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Single
class ApiaryRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFireStore: FirebaseFirestore,
    private val context: Context
) : ApiaryRepository {
    override suspend fun getApiaries(): List<ApiaryModel> = suspendCoroutine { continuation ->
        val apiaryList = mutableListOf<ApiaryModel>()
        val hiveTasks = mutableListOf<Task<QuerySnapshot>>()

        firebaseAuth.currentUser?.let { currentUser ->
            firebaseFireStore
                .collection("apiaries")
                .where(
                    Filter.or(
                        Filter.equalTo("uid", currentUser.uid),
                    )
                )
                .orderBy("timestamp", Query.Direction.ASCENDING)
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

                    Tasks.whenAllComplete(hiveTasks)
                        .addOnCompleteListener {
                            continuation.resume(apiaryList)
                        }
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        } ?: continuation.resume(apiaryList)
    }

    override suspend fun getApiaryById(apiaryId: String): ApiaryModel? =
        suspendCoroutine { continuation ->
            val apiaryTasks = mutableListOf<Task<QuerySnapshot>>()

            firebaseAuth.currentUser?.let {
                firebaseFireStore
                    .collection("apiaries")
                    .document(apiaryId)
                    .get()
                    .addOnSuccessListener { documentSnapshot ->
                        val apiaryData = documentSnapshot.data

                        apiaryData?.let { data ->
                            var apiary = ApiaryModel(
                                id = data["id"] as? String ?: "",
                                uid = data["uid"] as? String ?: "",
                                name = data["name"] as? String ?: "",
                                type = (data["type"] as? Number)?.toInt() ?: 0,
                                location = data["location"] as? String ?: "",
                                timestamp = data["timestamp"] as? Timestamp ?: Timestamp.now(),
                            )

                            val hivesCountTask = FirebaseFirestore.getInstance()
                                .collection("hives")
                                .whereEqualTo("apiaryId", apiaryId)
                                .get()
                                .addOnSuccessListener { hiveSnapshot ->
                                    apiary = apiary.copy(hivesCount = hiveSnapshot.documents.size)
                                }
                            apiaryTasks.add(hivesCountTask)

                            val lastOverviewTask = FirebaseFirestore.getInstance()
                                .collection("overviews")
                                .whereEqualTo("apiaryId", apiaryId)
                                .orderBy("timestamp", Query.Direction.ASCENDING)
                                .get()
                                .addOnSuccessListener {
                                    apiary = apiary.copy(
                                        lastOverview = LocalDate.now()
                                    )
                                }
                            apiaryTasks.add(lastOverviewTask)

                            Tasks.whenAllComplete(apiaryTasks)
                                .addOnCompleteListener {
                                    continuation.resume(apiary)
                                }
                        }
                    }
                    .addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
            } ?: continuation.resume(null)
        }

    override suspend fun createApiary(apiaryModel: ApiaryModel): CreateApiaryState =
        suspendCancellableCoroutine { continuation ->
            if (firebaseAuth.currentUser != null) {
                val docRef = firebaseFireStore
                    .collection("apiaries")
                    .document()
                val id = docRef.id

                val apiary = ApiaryModel(
                    id = id,
                    uid = firebaseAuth.currentUser!!.uid,
                    name = apiaryModel.name,
                    type = apiaryModel.type,
                    location = apiaryModel.location,
                    timestamp = Timestamp.now(),
                )

                docRef.set(apiary)

                continuation.resume(CreateApiaryState.Redirect(id))
            } else {
                continuation.resume(CreateApiaryState.Error(context.getString(R.string.hive_state_no_user)))
            }
        }

    override suspend fun editApiary(apiaryModel: ApiaryModel): CreateApiaryState =
        suspendCancellableCoroutine { continuation ->
            if (firebaseAuth.currentUser != null) {
                firebaseAuth.currentUser?.let {
                    firebaseFireStore
                        .collection("apiaries")
                        .document(apiaryModel.id)
                        .update(
                            mapOf(
                                "name" to apiaryModel.name,
                                "type" to apiaryModel.type,
                                "location" to apiaryModel.location,
                            ),
                        )

                    continuation.resume(CreateApiaryState.Redirect(apiaryId = apiaryModel.id))
                }
            } else {
                continuation.resume(CreateApiaryState.Error("hive_state_no_user"))
            }
        }

    override suspend fun removeApiary(
        apiaryId: String,
    ): RemoveApiaryState =
        suspendCancellableCoroutine { continuation ->
            if (firebaseAuth.currentUser != null) {
                firebaseAuth.currentUser?.let { currentUser ->
                    firebaseFireStore
                        .collection("apiaries")
                        .document(apiaryId)
                        .delete()
                        .addOnSuccessListener {
                            firebaseFireStore
                                .collection("hives")
                                .whereEqualTo("apiaryId", apiaryId)
                                .get()
                                .addOnSuccessListener { documents ->
                                    for (document in documents) {
                                        document.reference.delete()
                                            .addOnFailureListener { e ->
                                                continuation.resume(RemoveApiaryState.Error("Error deleting document: $e"))
                                            }
                                    }
                                }
                                .addOnFailureListener {
                                    continuation.resume(RemoveApiaryState.Error("exception"))
                                }

                            continuation.resume(RemoveApiaryState.Success)
                        }
                        .addOnFailureListener {
                            continuation.resume(RemoveApiaryState.Error("exception"))
                        }
                }
            } else {
                continuation.resume(RemoveApiaryState.Error("hive_state_no_user"))
            }
        }
}