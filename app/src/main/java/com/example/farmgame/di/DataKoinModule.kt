package com.example.FarmGame.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module(includes = [FirebaseModule::class])
@ComponentScan("com.example.FarmGame.data.repository")
class DataKoinModule