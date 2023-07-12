package com.sheikh.application.myapplication.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.chaquo.python.Python
import com.sheikh.application.myapplication.data.DefaultDownloaderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

class VideoInfoWorker(ctx: Context, params: WorkerParameters): CoroutineWorker(ctx, params) {

    companion object {
        const val VIDEO_URL = "videoUrl"
        private const val TAG = "VideoInfoWorker"

        const val VIDEO_TITLE = "videoTitle"
        const val VIDEO_THUMBNAIL = "videoThumbnail"
        const val VIDEO_STREAMS = "videoStreams"
        const val AUDIO_STREAMS = "audioStreams"

        const val TAG_OUTPUT = "tagOutput"

        const val WORK_NAME = "videoInfo"
    }

    private val repository = DefaultDownloaderRepository(Python.getInstance())

    override suspend fun doWork(): Result {
        val videoUrl = inputData.getString(VIDEO_URL) ?: ""

        Log.d(TAG, "Getting video Info -> $videoUrl")

        return withContext(Dispatchers.IO) {
            return@withContext try {
                val videoTitle =  async { repository.getTitle(videoUrl) }
                val videoThumbnail = async { repository.getThumbnail(videoUrl) }
                val videoStreams =  async { repository.getVideoStreams(videoUrl) }
                val audioStreams =  async { repository.getAudioStreams(videoUrl) }

                val outputData = workDataOf(
                    Pair(VIDEO_TITLE, videoTitle.await()),
                    Pair(VIDEO_THUMBNAIL, videoThumbnail.await()),
                    Pair(VIDEO_STREAMS, videoStreams.await()),
                    Pair(AUDIO_STREAMS, audioStreams.await())
                    )

                Result.success(outputData)
            }catch (throwable: Throwable) {
                Log.e(TAG, "video info failed", throwable)
                Result.failure()
            }
        }
    }
}