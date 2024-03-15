package com.example.pszzapp.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class FirebaseModule {
    @Single
    fun firebaseAuth(): FirebaseAuth = Firebase.auth

    @Single
    fun firebaseFireStore(): FirebaseFirestore = Firebase.firestore
}