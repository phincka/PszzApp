package com.example.pszzapp.data.repository

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import com.example.pszzapp.R
import com.example.pszzapp.data.model.ApiaryModel
import com.example.pszzapp.data.model.HiveModel
import com.example.pszzapp.domain.repository.ApiaryRepository
import com.example.pszzapp.domain.repository.HiveRepository
import com.example.pszzapp.presentation.apiaries.create.CreateApiaryState
import com.example.pszzapp.presentation.hive.create.CreateHiveState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.suspendCancellableCoroutine
import org.koin.core.annotation.Single
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
                    .collection("pszzApp")
                    .document(currentUser.uid)
                    .collection("apiaries")
                    .document(id)
                    .collection("hives")
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        println("=========")
                        println(querySnapshot.documents)
                        println("=========")
                        for (document in querySnapshot.documents) {
                            val apiaryData = document.data

                            apiaryData?.let { data ->
                                val hive = HiveModel(
                                    id = document.id,
                                    name = data["name"] as? String ?: "",
                                    apiaryId = data["apiaryId"] as? String ?: "",
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

    override suspend fun createHive(hiveModel: HiveModel): CreateHiveState =
        suspendCancellableCoroutine { continuation ->
            if (firebaseAuth.currentUser != null) {
                val hive = HiveModel(
                    id = "",
                    name = hiveModel.name,
                    apiaryId = hiveModel.apiaryId,
                )

                val docRef = firebaseFireStore
                        .collection("pszzApp")
                        .document(firebaseAuth.currentUser!!.uid)
                        .collection("apiaries")
                        .document(hiveModel.apiaryId)
                        .collection("hives")
                        .document()
                val id = docRef.id
                docRef.set(hive)

                continuation.resume(CreateHiveState.Redirect(id))
            } else {
                continuation.resume(CreateHiveState.Error(context.getString(R.string.hive_state_no_user)))
            }
        }
}