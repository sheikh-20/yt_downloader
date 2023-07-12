package com.sheikh.application.myapplication.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.sheikh.application.myapplication.utility.makeStatusNotification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import java.io.File

class CleanerWorker(ctx: Context, params: WorkerParameters): CoroutineWorker(ctx, params) {
    companion object {
        private const val TAG = "CleanerWorker"
    }

    override suspend fun doWork(): Result {
        Log.d(TAG, "Cleaning files")

        makeStatusNotification(
            message = "Cleaning files",
            context = applicationContext)

        return withContext(Dispatchers.IO) {
            return@withContext try {
                val clean = listOf(
                    async {  removeVideoDirectory() },
                    async {  removeAudioDirectory() },
                    async {  createOutputDirectory() }
                )

                clean.awaitAll()
                Result.success()
            } catch (throwable: Throwable) {
                Log.e(TAG, "Cleaning failed", throwable)
                Result.failure()
            }
        }
    }

    private fun removeVideoDirectory() {
        val dir = File(applicationContext.filesDir, "video")
        if (dir.exists()) {
            dir.deleteRecursively()
            Log.d(TAG, "Video dir deleted")
        }
    }

    private fun removeAudioDirectory() {
        val dir = File(applicationContext.filesDir, "audio")
        if (dir.exists()) {
            dir.deleteRecursively()
            Log.d(TAG, "Audio dir deleted")
        }
    }

    private fun createOutputDirectory() {
        val dir = File(applicationContext.filesDir, "output")
        if (!dir.exists()) {
            dir.mkdir()

            val file = File(dir, "output.mp4")
            if (!file.exists()) {
                file.createNewFile()
            }
        } else {
            val file = File(dir, "output.mp4")
            if (!file.exists()) {
                file.createNewFile()
            }
        }
    }
}