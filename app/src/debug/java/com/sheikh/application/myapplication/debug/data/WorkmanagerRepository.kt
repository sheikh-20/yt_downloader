package com.sheikh.application.myapplication.debug.data

import android.content.Context
import androidx.work.WorkInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.mapNotNull
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.sheikh.application.myapplication.debug.model.Stream
import com.sheikh.application.myapplication.debug.worker.CleanerWorker
import com.sheikh.application.myapplication.debug.worker.DownloadWorker
import com.sheikh.application.myapplication.debug.worker.MergeWorker
import com.sheikh.application.myapplication.debug.worker.VideoInfoWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

interface WorkmanagerRepository {
    fun readVideoInfo(): Flow<WorkInfo>
    suspend fun getVideoInfo(videoUrl: String)
    suspend fun videoDownload(videoUrl: String, videoStream: Stream, audioStream: Stream?)
}

class DefaultWorkmanagerRepository(context: Context): WorkmanagerRepository {

    private val workManager = WorkManager.getInstance(context)

    override fun readVideoInfo(): Flow<WorkInfo> =
        workManager.getWorkInfosByTagLiveData(VideoInfoWorker.TAG_OUTPUT).asFlow().mapNotNull {
            if (it.isNotEmpty()) it.first() else null
        }

    override suspend fun getVideoInfo(videoUrl: String) {
        val videoInfoBuilder = OneTimeWorkRequestBuilder<VideoInfoWorker>()
        videoInfoBuilder.setInputData(
            Data.Builder()
                .putString(VideoInfoWorker.VIDEO_URL, videoUrl)
                .build()
        )
        videoInfoBuilder.addTag(VideoInfoWorker.TAG_OUTPUT)

        workManager.enqueueUniqueWork(VideoInfoWorker.WORK_NAME, ExistingWorkPolicy.REPLACE,videoInfoBuilder.build())
    }

    override suspend fun videoDownload(videoUrl: String, videoStream: Stream, audioStream: Stream?)  {
        val formattedResultVideo = videoStream.iTag.substring(1, videoStream.iTag.length.minus(1))
        val formattedResultAudio = audioStream?.iTag?.substring(1, audioStream.iTag.length.minus(1)) ?: "0"

        val downloadBuilder = OneTimeWorkRequestBuilder<DownloadWorker>()

        downloadBuilder.setInputData(
            Data.Builder().putString(DownloadWorker.VIDEO_URL, videoUrl)
                .putInt(DownloadWorker.VIDEO_ITAG, formattedResultVideo.toInt())
                .putInt(DownloadWorker.AUDIO_ITAG, formattedResultAudio.toInt())
                .build())

        workManager.beginUniqueWork("download", ExistingWorkPolicy.REPLACE, OneTimeWorkRequestBuilder<CleanerWorker>().build())
            .then(downloadBuilder.build())
            .then(OneTimeWorkRequestBuilder<MergeWorker>().build())
            .enqueue()
    }
}