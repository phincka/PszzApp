package com.example.FarmGame

import android.app.Application
import com.example.FarmGame.di.AppModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module

class FarmGame : Application(){
    override fun onCreate() {
        super.onCreate()

        startKoin{
            androidContext( this@FarmGame)
            modules(
                AppModule().module,
            )
        }
    }
}