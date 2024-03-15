package com.example.pszzapp.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module(includes = [DataKoinModule::class])
@ComponentScan("com.example.pszzapp")
class AppModule