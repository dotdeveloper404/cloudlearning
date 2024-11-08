package com.sparkmembership.sparkowner

import android.app.Application
import com.google.firebase.FirebaseApp
import com.sparkmembership.sparkowner.util.InternetManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SparkOwner : Application() {

    companion object {
        lateinit var ctx: SparkOwner
    }

    override fun onCreate() {
        super.onCreate()
        ctx = this

        FirebaseApp.initializeApp(this)
        InternetManager.initialize(this)

    }

}