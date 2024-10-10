package com.example.pszzapp.data.repository

import android.content.Context
import android.util.Log
import com.example.pszzapp.R
import com.example.pszzapp.data.model.UserModel
import com.example.pszzapp.data.util.AccountUserState
import com.example.pszzapp.data.util.AuthState
import com.example.pszzapp.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import org.koin.core.annotation.Single
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


@Single
class AuthRepositoryImpl(
    private val firebaseFireStore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val context: Context
): AuthRepository {
    override suspend fun getCurrentUser(): AccountUserState =
        suspendCoroutine { continuation ->
            firebaseAuth.currentUser?.let { currentUser ->
                firebaseFireStore
                    .collection("users")
                    .whereEqualTo("uid", currentUser.uid)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        for (document in querySnapshot.documents) {
                            val listData = document.data

                            listData?.let { data ->
                                val userModel = UserModel(
                                    uid = document.id,
                                    name = data["name"] as? String ?: "",
                                    email = data["email"] as? String ?: "",
                                    isModalAlternativeEnable = data["modalAlternativeEnable"] as? Boolean
                                        ?: false,
                                )
                                continuation.resume(AccountUserState.SignedInState(userModel))
                            }
                        }
                    }
                    .addOnFailureListener { exception ->
                        exception.message?.let { AccountUserState.Error(it) }
                            ?.let { continuation.resume(it) }
                    }
            } ?: continuation.resume(AccountUserState.GuestState)
        }

    override suspend fun firebaseEmailSignIn(
        email: String,
        password: String
    ): AuthState {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            AuthState.Success(true)
        } catch (error: Exception) {
            AuthState.Error(error.toString())
        }
    }

    override suspend fun firebaseEmailSignUp(
        name: String,
        email: String,
        password: String,
        repeatPassword: String
    ): AuthState {
        return if (password == repeatPassword) {
            try {
                firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                firebaseEmailSignIn(email, password)

                firebaseAuth.currentUser?.let { currentUser ->
                    val docRef = firebaseFireStore
                        .collection("users")
                        .document()

                    val userModel = UserModel(
                        uid = currentUser.uid,
                        name = name,
                        email = email,
                        isModalAlternativeEnable = false,
                    )

                    docRef.set(userModel)

                    AuthState.Success(true)
                } ?:  AuthState.Error(context.getString(R.string.auth_state_passwords_not_equal))

            } catch (error: Exception) {
                AuthState.Error(error.toString())
            }
        } else {
            AuthState.Error(context.getString(R.string.auth_state_passwords_not_equal))
        }
    }

    override suspend fun firebaseSignOut() = try {
        firebaseAuth.signOut()
        AccountUserState.GuestState
    } catch (error: Exception) {
        AccountUserState.Error(error.toString())
    }

    override suspend fun checkEmailVerification(): AuthState {
        val currentUser = firebaseAuth.currentUser

        return if (currentUser != null) {
            try {
                currentUser.reload().await()

                if (currentUser.isEmailVerified) {
                    AuthState.Success(true)
                } else {
                    AuthState.Success(
                        false,
                        message = context.getString(R.string.auth_state_verification_error)
                    )
                }
            } catch (e: Exception) {
                AuthState.Error("${context.getString(R.string.auth_state_update_error)} ${e.message}")
            }
        } else {
            AuthState.Error(context.getString(R.string.auth_state_no_user))
        }
    }

    override suspend fun resendVerificationEmail(): AuthState {
        val currentUser = firebaseAuth.currentUser

        return if (currentUser != null) {
            try {
                currentUser.sendEmailVerification()
                AuthState.Success(
                    success = false,
                    message = context.getString(R.string.auth_state_email_send)
                )
            } catch (e: Exception) {
                AuthState.Error("${context.getString(R.string.auth_state_email_not_send)} ${e.message}")
            }
        } else {
            AuthState.Error(context.getString(R.string.auth_state_no_user))
        }
    }
}