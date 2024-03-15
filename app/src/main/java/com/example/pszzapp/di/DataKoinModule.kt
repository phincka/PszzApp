package com.example.pszzapp.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module(includes = [FirebaseModule::class])
@ComponentScan("com.example.pszzapp.data.repository")
class DataKoinModule