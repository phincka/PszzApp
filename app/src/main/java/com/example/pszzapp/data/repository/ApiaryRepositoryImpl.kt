package com.example.pszzapp.data.repository

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import com.example.pszzapp.R
import com.example.pszzapp.data.model.ApiaryModel
import com.example.pszzapp.data.model.HiveModel
import com.example.pszzapp.domain.repository.ApiaryRepository
import com.example.pszzapp.presentation.apiaries.create.CreateApiaryState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.suspendCancellableCoroutine
import org.koin.core.annotation.Single
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

        firebaseAuth.currentUser?.let { currentUser ->
            firebaseFireStore.collection("pszzApp")
                .document(currentUser.uid)
                .collection("apiaries")
                .get()
                .addOnSuccessListener { querySnapshot ->
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

    override suspend fun createApiary(apiaryModel: ApiaryModel): CreateApiaryState =
        suspendCancellableCoroutine { continuation ->
            if (firebaseAuth.currentUser != null) {
                val apiary = ApiaryModel(
                    id = "",
                    name = apiaryModel.name,
                    type = apiaryModel.type,
                )


                val docRef = firebaseFireStore
                        .collection("pszzApp")
                        .document(firebaseAuth.currentUser!!.uid)
                        .collection("apiaries")
                        .document()
                val id = docRef.id
                docRef.set(apiary)

                continuation.resume(CreateApiaryState.Redirect(id))
            } else {
                continuation.resume(CreateApiaryState.Error(context.getString(R.string.hive_state_no_user)))
            }
        }

}