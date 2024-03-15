package com.example.pszzapp.data.repository

import android.util.Log
import com.example.pszzapp.data.model.ApiaryModel
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
                    println(querySnapshot.documents)
                    println("----------")


                    for (document in querySnapshot.documents) {
                        val apiaryData = document.data



                        apiaryData?.let { data ->
                            val apiary = ApiaryModel(
                                name = data["name"] as? String ?: "",
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
}