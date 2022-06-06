package com.canerture.videocallingapp

import android.app.Application
import android.util.Log
import com.canerture.videocallingapp.common.webrtc.CloudDbWrapper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CallingApp : Application() {
    override fun onCreate() {
        super.onCreate()
        CloudDbWrapper.initialize(this) {
            Log.i("Application", it.toString())
        }
    }
}