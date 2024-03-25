package com.example.pszzapp.data.repository

import androidx.compose.runtime.mutableStateOf
import com.example.pszzapp.data.model.ApiaryModel
import com.example.pszzapp.data.model.HiveModel
import com.example.pszzapp.domain.repository.ApiaryRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.core.annotation.Single
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Single
class ApiaryRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFireStore: FirebaseFirestore
): ApiaryRepository {
    override suspend fun getApiaries(): List<ApiaryModel> = suspendCoroutine { continuation ->
        val apiaryList = mutableListOf<ApiaryModel>()

        firebaseAuth.currentUser?.let { currentUser ->
            firebaseFireStore.collection("pszzApp")
                .document(currentUser.uid)
                .collection("apiaries")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    println("----------")
                    println(querySnapshot.metadata.isFromCache)
                    println("----------")


                    for (document in querySnapshot.documents) {
                        val apiaryData = document.data



                        apiaryData?.let { data ->
                            val apiary = ApiaryModel(
                                id = document.id,
                                name = data["name"] as? String ?: "",
                                type = 0,
                            )
                            apiaryList.add(apiary)
                        }
                    }
                    continuation.resume(apiaryList)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        } ?: continuation.resume(apiaryList)
    }

    override suspend fun getHivesByApiaryId(id: String): List<HiveModel> = suspendCoroutine { continuation ->
        val hivesList = mutableListOf<HiveModel>()

        firebaseAuth.currentUser?.let { currentUser ->
            firebaseFireStore.collection("pszzApp")
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
}