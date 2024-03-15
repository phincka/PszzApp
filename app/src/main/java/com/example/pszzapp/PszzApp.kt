package com.example.pszzapp

import android.app.Application
import com.example.pszzapp.di.AppModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module

class PszzApp : Application(){
    override fun onCreate() {
        super.onCreate()

        startKoin{
            androidContext( this@PszzApp)
            modules(
                AppModule().module,
            )
        }
    }
}