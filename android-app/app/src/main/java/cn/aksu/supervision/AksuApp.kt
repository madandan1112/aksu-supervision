package cn.aksu.supervision

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AksuApp : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: AksuApp
            private set
    }
}
