package com.sheikh.application.myapplication.debug.data

import android.content.Context
import android.util.Log
import com.chaquo.python.Python

private const val TAG = "AppContainer"
interface AppContainer {
    val repository: DownloaderRepository
    val workManagerRepository: WorkmanagerRepository
}

class DefaultAppContainer(private val context: Context): AppContainer {
    override val repository: DownloaderRepository by lazy {
        DefaultDownloaderRepository(Python.getInstance())
    }
    override val workManagerRepository: WorkmanagerRepository by lazy {
        DefaultWorkmanagerRepository(context)
    }
}