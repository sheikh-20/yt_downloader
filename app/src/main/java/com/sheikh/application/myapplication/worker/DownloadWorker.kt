package com.sheikh.application.myapplication.worker

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.chaquo.python.Python
import com.sheikh.application.myapplication.data.DefaultDownloaderRepository
import com.sheikh.application.myapplication.utility.makeStatusNotification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okio.IOException

class DownloadWorker(ctx: Context, params: WorkerParameters): CoroutineWorker(ctx, params) {

    companion object {
        private const val TAG = "DownloadWorker"
        const val VIDEO_URL = "videoUrl"
        const val VIDEO_ITAG = "video_itag"
        const val AUDIO_ITAG = "audio_itag"
    }

    private val repository = DefaultDownloaderRepository(Python.getInstance())

    override suspend fun doWork(): Result {
        val videoUrl = inputData.getString(VIDEO_URL) ?: ""
        val videoItag = inputData.getInt(VIDEO_ITAG, 0)
        val audioItag = inputData.getInt(AUDIO_ITAG, 0)

        delay(3_000L)

        makeStatusNotification(
            message = "Downloading files",
            context = applicationContext)

        return withContext(Dispatchers.IO) {

            return@withContext try {

                val task = listOf(
                    async {  repository.videoDownload(videoUrl, videoItag) },
                    async {  repository.audioDownload(videoUrl, audioItag) }
                )

                task.awaitAll()
                Result.success()
            } catch (throwable: Throwable) {
                Log.e(TAG, "Video download failed", throwable)
                Result.failure()
            }
        }
    }
}