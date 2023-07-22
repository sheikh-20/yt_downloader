package com.sheikh.application.myapplication.debug

import android.app.Application
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.sheikh.application.myapplication.debug.data.AppContainer
import com.sheikh.application.myapplication.debug.data.DefaultAppContainer

class DownloaderApplication: Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }
        container = DefaultAppContainer(applicationContext)
    }
}