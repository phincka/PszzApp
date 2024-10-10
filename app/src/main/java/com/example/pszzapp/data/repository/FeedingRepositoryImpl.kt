package com.example.pszzapp.data.repository

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import com.example.pszzapp.R
import com.example.pszzapp.data.model.ApiaryModel
import com.example.pszzapp.data.model.CreateFeedingModel
import com.example.pszzapp.data.model.FeedingModel
import com.example.pszzapp.data.model.HiveModel
import com.example.pszzapp.domain.repository.ApiaryRepository
import com.example.pszzapp.domain.repository.FeedingRepository
import com.example.pszzapp.domain.repository.HiveRepository
import com.example.pszzapp.presentation.apiary.create.CreateApiaryState
import com.example.pszzapp.presentation.feeding.CreateFeedingState
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
class FeedingRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFireStore: FirebaseFirestore,
    private val context: Context
) : FeedingRepository {
    override suspend fun getFeedingsByHiveId(hiveId: String): List<FeedingModel> =
        suspendCoroutine { continuation ->
            val feedingList = mutableListOf<FeedingModel>()

            firebaseAuth.currentUser?.let { currentUser ->
                firebaseFireStore
                    .collection("feedings")
                    .whereEqualTo("hiveId", hiveId)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        for (document in querySnapshot.documents) {
                            val feedingData = document.data

                            feedingData?.let { data ->
                                val feedingModel = FeedingModel(
                                    id = document.id,
                                    uid = currentUser.uid,
                                    apiaryId = data["apiaryId"] as? String ?: "",
                                    hiveId = data["hiveId"] as? String ?: "",
                                    concentration = data["concentration"] as? String ?: "",
                                    sugarAmount = data["sugarAmount"] as? String ?: "",
                                    syrupAmount = data["syrupAmount"] as? String ?: "",
                                    waterAmount = data["waterAmount"] as? String ?: "",
                                    cakeAmount = data["cakeAmount"] as? String ?: "",
                                    feedingDate = data["feedingDate"] as? LocalDate ?: LocalDate.now(),
                                )
                                feedingList.add(feedingModel)
                            }
                        }
                        continuation.resume(feedingList)
                    }
                    .addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
            } ?: continuation.resume(feedingList)
        }

    override suspend fun createFeeding(createFeedingModel: CreateFeedingModel): CreateFeedingState =
        suspendCancellableCoroutine { continuation ->
            if (firebaseAuth.currentUser != null) {
                val docRef = firebaseFireStore
                    .collection("feedings")
                    .document()
                val feedingId = docRef.id

                val feeding = FeedingModel(
                    id = feedingId,
                    uid = "firebaseAuth.currentUser.uid",
                    apiaryId = createFeedingModel.apiaryId,
                    hiveId = createFeedingModel.hiveId,
                    concentration = createFeedingModel.concentration,
                    sugarAmount = createFeedingModel.sugarAmount,
                    syrupAmount = createFeedingModel.syrupAmount,
                    waterAmount = createFeedingModel.waterAmount,
                    cakeAmount = createFeedingModel.cakeAmount,
                    feedingDate = createFeedingModel.feedingDate,
                )

                docRef.set(feeding)
                continuation.resume(CreateFeedingState.Redirect(createFeedingModel.hiveId))
            } else {
                continuation.resume(CreateFeedingState.Error(context.getString(R.string.hive_state_no_user)))
            }
        }

    override suspend fun removeFeeding(feedingId: String): CreateFeedingState =
        suspendCancellableCoroutine { continuation ->
            if (firebaseAuth.currentUser != null) {
                val docRef = firebaseFireStore
                    .collection("feedings")
                    .whereEqualTo("docId", feedingId)

                docRef.get()

                continuation.resume(CreateFeedingState.Redirect(feedingId))
            } else {
                continuation.resume(CreateFeedingState.Error(context.getString(R.string.hive_state_no_user)))
            }
        }
}