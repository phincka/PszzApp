package com.example.pszzapp.data.repository

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import com.example.pszzapp.R
import com.example.pszzapp.data.model.ApiaryModel
import com.example.pszzapp.data.model.HiveModel
import com.example.pszzapp.domain.repository.ApiaryRepository
import com.example.pszzapp.domain.repository.HiveRepository
import com.example.pszzapp.presentation.apiary.create.CreateApiaryState
import com.example.pszzapp.presentation.hive.create.CreateHiveState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import org.koin.core.annotation.Single
import java.time.LocalDate
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

            firebaseAuth.currentUser?.let { currentUser ->
                firebaseFireStore
                    .collection("hives")
                    .whereEqualTo("apiaryId", id)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        for (document in querySnapshot.documents) {
                            val apiaryData = document.data

                            apiaryData?.let { data ->
                                val hive = HiveModel(
                                    id = document.id,
                                    uid = currentUser.uid,
                                    apiaryId = data["apiaryId"] as? String ?: "",
                                    name = data["name"] as? String ?: "",
                                    familyType = data["familyType"] as? Int ?: 0,
                                    hiveType = data["hiveType"] as? Int ?: 0,
                                    breed = data["breed"] as? Int ?: 0,
                                    line = data["line"] as? String ?: "",
                                    state = data["state"] as? Int ?: 0,
                                    queenYear = data["queenYear"] as? Int ?: 0,
                                    queenAddedDate = data["queenAddedDate"] as? LocalDate ?: LocalDate.now(),
                                    hiveCreatedDate = data["hiveCreatedDate"] as? LocalDate ?: LocalDate.now(),
                                    queenNote = data["queenNote"] as? String ?: "",
                                )
                                hivesList.add(hive)
                            }
                        }
                        continuation.resume(hivesList)
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
                                val hive = HiveModel(
                                    id = id,
                                    uid = data["uid"] as? String ?: "",
                                    apiaryId = data["apiaryId"] as? String ?: "",
                                    name = data["name"] as? String ?: "",
                                    familyType = data["familyType"] as? Int ?: 0,
                                    hiveType = data["hiveType"] as? Int ?: 0,
                                    breed = data["breed"] as? Int ?: 0,
                                    line = data["line"] as? String ?: "",
                                    state = data["state"] as? Int ?: 0,
                                    queenYear = data["queenYear"] as? Int ?: 0,
                                    queenAddedDate = data["queenAddedDate"] as? LocalDate ?: LocalDate.now(),
                                    hiveCreatedDate = data["hiveCreatedDate"] as? LocalDate ?: LocalDate.now(),
                                    queenNote = data["queenNote"] as? String ?: ""
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
                continuation.resume(CreateHiveState.Redirect(hiveId))
            } else {
                continuation.resume(CreateHiveState.Error(context.getString(R.string.hive_state_no_user)))
            }
        }

    override suspend fun removeHive(id: String): CreateHiveState =
        suspendCancellableCoroutine { continuation ->
            if (firebaseAuth.currentUser != null) {
                val docRef = firebaseFireStore
                    .collection("hives")
                    .whereEqualTo("docId", id)

                docRef.get()

                continuation.resume(CreateHiveState.Redirect(id))
            } else {
                continuation.resume(CreateHiveState.Error(context.getString(R.string.hive_state_no_user)))
            }
        }
}