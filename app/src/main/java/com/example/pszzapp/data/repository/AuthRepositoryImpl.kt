package com.example.pszzapp.data.repository

import android.content.Context
import com.example.pszzapp.R
import com.example.pszzapp.data.model.UserModel
import com.example.pszzapp.data.util.AuthState
import com.example.pszzapp.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import org.koin.core.annotation.Single


@Single
class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val context: Context
): AuthRepository {
    override suspend fun getCurrentUser(): UserModel? = firebaseAuth.currentUser?.run {
        UserModel(
            userId = uid,
            email = email,
            isEmailVerified = isEmailVerified
        )
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
        email: String,
        password: String,
        repeatPassword: String
    ): AuthState {
        return if (password == repeatPassword) {
            try {
                firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                firebaseEmailSignIn(email, password)

                val currentUser = firebaseAuth.currentUser
                if (currentUser != null) {
                    currentUser.sendEmailVerification().await()
                    AuthState.Success(true)
                } else {
                    AuthState.Error(context.getString(R.string.auth_state_no_user))
                }
            } catch (error: Exception) {
                AuthState.Error(error.toString())
            }
        } else {
            AuthState.Error(context.getString(R.string.auth_state_passwords_not_equal))
        }
    }

    override suspend fun firebaseSignOut() = try {
        firebaseAuth.signOut()
        AuthState.Success(true)
    } catch (error: Exception) {
        AuthState.Error(error.toString())
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