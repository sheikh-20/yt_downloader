package com.sheikh.application.myapplication.debug.viewmodel

import android.app.Application
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL
import com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS
import com.arthenica.mobileffmpeg.FFmpeg
import com.sheikh.application.myapplication.debug.DownloaderApplication
import com.sheikh.application.myapplication.debug.data.DownloaderRepository
import com.sheikh.application.myapplication.debug.data.WorkmanagerRepository
import com.sheikh.application.myapplication.debug.model.Stream
import com.sheikh.application.myapplication.debug.utility.getStream
import com.sheikh.application.myapplication.debug.worker.VideoInfoWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.UUID

sealed interface DownloadUiState {
    object Default: DownloadUiState
    object Loading: DownloadUiState
    data class Complete(val videoTitle: String = "",
                        val videoThumbnail: String = "",
                        val videoStreams: List<Stream> = emptyList(),
                        val audioStreams: Stream? = null,
                        ): DownloadUiState
}
class DownloaderViewModel(private val repository: DownloaderRepository,
                          private val workmanagerRepository: WorkmanagerRepository,
                          private val application: Application): ViewModel() {

    private val workManager = WorkManager.getInstance(application)

    val downloaderUiState: StateFlow<DownloadUiState> = workmanagerRepository.readVideoInfo()
        .map { info ->
            val videoTitle = info.outputData.getString(VideoInfoWorker.VIDEO_TITLE)
            val videoThumbnail = info.outputData.getString(VideoInfoWorker.VIDEO_THUMBNAIL)
            val videoStreams = info.outputData.getString(VideoInfoWorker.VIDEO_STREAMS)
            val audioStreams = info.outputData.getString(VideoInfoWorker.AUDIO_STREAMS)

            when {
                info.state.isFinished && !videoTitle.isNullOrEmpty() && !videoThumbnail.isNullOrEmpty() && !videoStreams.isNullOrEmpty() && !audioStreams.isNullOrEmpty() -> {
                    DownloadUiState.Complete(
                        videoTitle = videoTitle,
                        videoThumbnail = videoThumbnail,
                        videoStreams = getVideoStreams(videoStreams),
                        audioStreams = getAudioStreams(audioStreams).last()
                        )
                }
                info.state == WorkInfo.State.CANCELLED -> {
                    DownloadUiState.Default
                }
                else -> DownloadUiState.Loading
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DownloadUiState.Default
        )

    fun getVideoInfo(videoUrl: String) = viewModelScope.launch {
        workmanagerRepository.getVideoInfo(videoUrl)
    }

    private fun getVideoStreams(videoStreams: String) : List<Stream> {
        val listResult = mutableListOf<Stream>()

        videoStreams.split(",").forEach {
            listResult.add(it.getStream)
        }
        return listResult
    }

    private fun getAudioStreams(audioStreams: String) : List<Stream> {
        val listResult = mutableListOf<Stream>()

        audioStreams.split(",").forEach {
            listResult.add(it.getStream)
        }
        return listResult
    }

    fun videoDownload(videoUrl: String, videoStream: Stream, audioStream: Stream?) = viewModelScope.launch {
        workmanagerRepository.videoDownload(videoUrl, videoStream, audioStream)
    }

    fun cancelWorker() {
        workManager.cancelUniqueWork(VideoInfoWorker.WORK_NAME)
    }

    companion object {
        private const val TAG = "DownloaderViewModel"

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as DownloaderApplication)
                val repository = application.container.repository
                val workmanagerRepository = application.container.workManagerRepository
                DownloaderViewModel(repository, workmanagerRepository, application)
            }
        }
    }
}