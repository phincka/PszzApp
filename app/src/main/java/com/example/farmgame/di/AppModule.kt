package com.example.FarmGame.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module(includes = [DataKoinModule::class])
@ComponentScan("com.example.FarmGame")
class AppModule